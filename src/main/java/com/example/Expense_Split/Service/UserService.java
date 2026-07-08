package com.example.Expense_Split.Service;

import com.example.Expense_Split.DTO.BalanceResponseDTO;
import com.example.Expense_Split.DTO.NetSettlementResponseDTO;
import com.example.Expense_Split.DTO.UserRequestDTO;
import com.example.Expense_Split.DTO.UserResponseDTO;
import com.example.Expense_Split.Entity.Split;
import com.example.Expense_Split.Entity.SplitStatus;
import com.example.Expense_Split.Entity.User;
import com.example.Expense_Split.Exception.InvalidOperationException;
import com.example.Expense_Split.Exception.UserNotFoundException;
import com.example.Expense_Split.Repository.SplitRepository;
import com.example.Expense_Split.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SplitRepository splitRepository;

    public UserService(UserRepository userRepository,
                       SplitRepository splitRepository) {
        this.userRepository = userRepository;
        this.splitRepository = splitRepository;
    }

    // 1️ Create User
    public UserResponseDTO createUser(UserRequestDTO request) {

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        User savedUser = userRepository.save(user);

        return mapToDTO(savedUser);
    }

    // 2️ Get User By Id
    public UserResponseDTO getUser(int id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id));

        return mapToDTO(user);
    }

    // 3️ Add Friend
    public String addFriend(int userId, int friendId) {

        if (userId == friendId) {
            throw new InvalidOperationException("User cannot add themselves as friend");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() ->
                        new UserNotFoundException("Friend not found with id: " + friendId));

        if (!user.getFriends().contains(friend)) {
            user.getFriends().add(friend);
            friend.getFriends().add(user);

            userRepository.save(user);
            userRepository.save(friend);
        }

        return "Friend added successfully";
    }
    // 4️ Get All Users
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // 5️ Delete User
    public String deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return "User deleted successfully";
    }

    // 6️ Balance Calculation Logic
    public BalanceResponseDTO getBalance(int userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Split> youOweSplits =
                splitRepository.findByFromUserIdAndStatus(userId, SplitStatus.UNPAID);

        List<Split> youAreOwedSplits =
                splitRepository.findByToUserIdAndStatus(userId, SplitStatus.UNPAID);

        // Group raw amounts by counterpart name
        Map<String, Double> youOwePerPerson = new HashMap<>();
        for (Split split : youOweSplits) {
            youOwePerPerson.merge(split.getToUser().getName(), split.getAmount(), Double::sum);
        }

        Map<String, Double> owedToYouPerPerson = new HashMap<>();
        for (Split split : youAreOwedSplits) {
            owedToYouPerPerson.merge(split.getFromUser().getName(), split.getAmount(), Double::sum);
        }

        // Collect all unique counterpart names
        Set<String> allPeople = new HashSet<>();
        allPeople.addAll(youOwePerPerson.keySet());
        allPeople.addAll(owedToYouPerPerson.keySet());

        // Build net breakdown per person
        Map<String, Double> breakdown = new HashMap<>();
        for (String person : allPeople) {
            double owe  = youOwePerPerson.getOrDefault(person, 0.0);
            double owed = owedToYouPerPerson.getOrDefault(person, 0.0);
            double net  = owe - owed;

            if (net > 0) {
                breakdown.put("You owe " + person, net);
            } else if (net < 0) {
                breakdown.put(person + " owes you", Math.abs(net));
            } else {
                breakdown.put("Settled with " + person, 0.0);
            }
        }

        double totalYouOwe     = youOweSplits.stream().mapToDouble(Split::getAmount).sum();
        double totalYouAreOwed = youAreOwedSplits.stream().mapToDouble(Split::getAmount).sum();
        double netBalance      = totalYouAreOwed - totalYouOwe;

        return new BalanceResponseDTO(totalYouOwe, totalYouAreOwed, netBalance, breakdown);
    }

    // 7️ Net Settlement — calculates only, does NOT modify database
    public NetSettlementResponseDTO getNetSettlement(int userAId, int userBId) {

        User userA = userRepository.findById(userAId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userAId));

        User userB = userRepository.findById(userBId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userBId));

        double aOwesB = splitRepository
                .findByFromUserIdAndToUserIdAndStatus(userAId, userBId, SplitStatus.UNPAID)
                .stream().mapToDouble(Split::getAmount).sum();

        double bOwesA = splitRepository
                .findByFromUserIdAndToUserIdAndStatus(userBId, userAId, SplitStatus.UNPAID)
                .stream().mapToDouble(Split::getAmount).sum();

        double net = aOwesB - bOwesA;

        String direction;
        if (net > 0) direction = userA.getName() + " pays " + userB.getName();
        else if (net < 0) direction = userB.getName() + " pays " + userA.getName();
        else direction = "Settled";

        return new NetSettlementResponseDTO(
                userA.getName(), userB.getName(),
                aOwesB, bOwesA, Math.abs(net), direction
        );
    }

    // 8️ Net Settle & Offset — calculates AND cancels splits in the database
    @Transactional
    public NetSettlementResponseDTO netSettleAndOffset(int userAId, int userBId) {

        User userA = userRepository.findById(userAId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userAId));

        User userB = userRepository.findById(userBId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userBId));

        // Fetch all unpaid splits in both directions
        List<Split> aOwesBSplits = splitRepository
                .findByFromUserIdAndToUserIdAndStatus(userAId, userBId, SplitStatus.UNPAID);

        List<Split> bOwesASplits = splitRepository
                .findByFromUserIdAndToUserIdAndStatus(userBId, userAId, SplitStatus.UNPAID);

        double aOwesB = aOwesBSplits.stream().mapToDouble(Split::getAmount).sum();
        double bOwesA = bOwesASplits.stream().mapToDouble(Split::getAmount).sum();

        double smaller = Math.min(aOwesB, bOwesA);

        // Cancel the smaller side fully — mark all those splits as PAID
        // then reduce from the larger side until the offset amount is consumed
        if (smaller > 0) {

            // Fully cancel whichever side is smaller (or equal)
            List<Split> smallerSide = (aOwesB <= bOwesA) ? aOwesBSplits : bOwesASplits;
            List<Split> largerSide  = (aOwesB <= bOwesA) ? bOwesASplits : aOwesBSplits;

            // Mark all splits on the smaller side as PAID (they are fully offset)
            for (Split split : smallerSide) {
                split.setStatus(SplitStatus.PAID);
                splitRepository.save(split);
            }

            // Reduce from the larger side by the offset amount
            double remaining = smaller;
            for (Split split : largerSide) {
                if (remaining <= 0) break;

                if (split.getAmount() <= remaining) {
                    // This split is fully covered by the offset — mark PAID
                    remaining -= split.getAmount();
                    split.setStatus(SplitStatus.PAID);
                } else {
                    // This split is partially covered — reduce its amount
                    split.setAmount(split.getAmount() - remaining);
                    remaining = 0;
                }
                splitRepository.save(split);
            }
        }

        // Recalculate final net after offsetting
        double net = Math.abs(aOwesB - bOwesA);

        String direction;
        if (aOwesB > bOwesA)      direction = userA.getName() + " pays " + userB.getName();
        else if (bOwesA > aOwesB) direction = userB.getName() + " pays " + userA.getName();
        else                       direction = "Settled";

        return new NetSettlementResponseDTO(
                userA.getName(), userB.getName(),
                aOwesB, bOwesA, net, direction
        );
    }

    // 🔁 Mapper
    private UserResponseDTO mapToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

    }
    public List<UserResponseDTO> getHighActivityUsers() {
        return userRepository.findHighActivityUsers()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
}
