// ── Base URL (hardcoded; no longer editable from the UI) ──
const BASE_URL = 'http://localhost:8080';
const BASE = () => BASE_URL;

// ── Section registry ──
const sections = ['dashboard','users','groups','expenses','transactions','balance','settlement','friends'];
const titles = {
    dashboard:    'Dashboard',
    users:        'Users',
    groups:       'Groups',
    expenses:     'Expenses',
    transactions: 'Transactions',
    balance:      'Balance Check',
    settlement:   'Net Settlement',
    friends:      'Add Friends'
};

// ── Navigation ──
function showSection(id) {
    sections.forEach(s => document.getElementById('section-' + s).classList.remove('active'));
    document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
    document.getElementById('section-' + id).classList.add('active');
    document.querySelectorAll('.nav-item').forEach(el => {
        if (el.textContent.trim().toLowerCase().includes(titles[id].toLowerCase().split(' ')[0]))
            el.classList.add('active');
    });
    document.getElementById('page-title').textContent = titles[id];
    if (id === 'dashboard') loadDashboardUsers();
}

// ── Generic fetch wrapper ──
async function api(method, path, body) {
    const opts = { method, headers: { 'Content-Type': 'application/json' } };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(BASE() + path, opts);
    const text = await res.text();
    let data;
    try { data = JSON.parse(text); } catch { data = text; }
    return { ok: res.ok, status: res.status, data };
}

// ── Set response box ──
function setRes(elId, result) {
    const el = document.getElementById(elId);
    el.textContent = typeof result.data === 'string' ? result.data : JSON.stringify(result.data, null, 2);
    el.className = 'response-box ' + (result.ok ? '' : 'error');
}

// ── Toast notifications ──
function toast(msg, type = 'success') {
    const c = document.getElementById('toast-container');
    const t = document.createElement('div');
    t.className = 'toast ' + type;
    t.innerHTML = `<span>${type === 'success' ? '✓' : '✕'}</span><span>${msg}</span>`;
    c.appendChild(t);
    setTimeout(() => t.remove(), 3500);
}

// ── API health check — updates both sidebar dot and topbar badge ──
async function checkStatus() {
    const sidebarDot  = document.getElementById('status-dot');
    const sidebarText = document.getElementById('status-text');
    const topbarDot   = document.getElementById('topbar-status-dot');
    const topbarText  = document.getElementById('topbar-status-text');

    try {
        const r = await fetch(BASE() + '/expenses/test', { signal: AbortSignal.timeout(3000) });
        if (r.ok) {
            if (sidebarDot)  sidebarDot.className  = 'status-dot online';
            if (sidebarText) sidebarText.textContent = 'API Connected';
            if (topbarDot)   topbarDot.className   = 'api-connected-dot online';
            if (topbarText)  topbarText.textContent = 'API Connected';
        } else { throw new Error(); }
    } catch {
        if (sidebarDot)  sidebarDot.className  = 'status-dot';
        if (sidebarText) sidebarText.textContent = 'API Offline';
        if (topbarDot)   topbarDot.className   = 'api-connected-dot';
        if (topbarText)  topbarText.textContent = 'API Offline';
    }
}

checkStatus();
setInterval(checkStatus, 15000);

// ════ USERS ════
async function createUser() {
    const name  = document.getElementById('user-name').value.trim();
    const email = document.getElementById('user-email').value.trim();
    if (!name || !email) return toast('Fill in name and email', 'error');
    const r = await api('POST', '/users', { name, email });
    setRes('user-create-res', r);
    if (r.ok) toast('User created: ' + (r.data.name || name));
    else toast('Failed to create user', 'error');
}

async function getUser() {
    const id = document.getElementById('get-user-id').value;
    if (!id) return toast('Enter a user ID', 'error');
    const r = await api('GET', '/users/' + id);
    setRes('get-user-res', r);
}

async function getAllUsers() {
    const r  = await api('GET', '/users');
    const el = document.getElementById('all-users-list');
    if (!r.ok || !Array.isArray(r.data) || r.data.length === 0) {
        el.innerHTML = '<div class="empty-state"><div class="empty-icon">👤</div><p>No users found.</p></div>';
        return;
    }
    el.innerHTML = `<div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Name</th><th>Email</th></tr></thead>
        <tbody>${r.data.map(u => `<tr>
            <td><span class="badge badge-blue">#${u.id}</span></td>
            <td style="color:var(--text);font-weight:500;">${u.name}</td>
            <td>${u.email}</td>
        </tr>`).join('')}</tbody>
    </table></div>`;
}

async function deleteUser() {
    const id = document.getElementById('delete-user-id').value;
    if (!id) return toast('Enter a user ID', 'error');
    const r = await api('DELETE', '/users/' + id);
    setRes('delete-user-res', r);
    if (r.ok) toast('User deleted');
    else toast('Failed to delete', 'error');
}

// ════ GROUPS ════
async function createGroup() {
    const name = document.getElementById('group-name').value.trim();
    if (!name) return toast('Enter a group name', 'error');
    const r = await api('POST', '/groups', { name });
    setRes('group-create-res', r);
    if (r.ok) toast('Group "' + name + '" created');
    else toast('Failed to create group', 'error');
}

async function getGroup() {
    const id = document.getElementById('get-group-id').value;
    if (!id) return toast('Enter a group ID', 'error');
    const r = await api('GET', '/groups/' + id);
    setRes('get-group-res', r);
}

async function addUserToGroup() {
    const gid = document.getElementById('add-user-group-id').value;
    const uid = document.getElementById('add-user-to-group-user-id').value;
    if (!gid || !uid) return toast('Enter both IDs', 'error');
    const r = await api('POST', `/groups/${gid}/add-user/${uid}`);
    setRes('add-group-user-res', r);
    if (r.ok) toast('User added to group');
    else toast('Failed to add user', 'error');
}

// ════ EXPENSES ════
async function addExpense() {
    const paidById    = parseInt(document.getElementById('expense-paidby').value);
    const groupId     = parseInt(document.getElementById('expense-group').value);
    const amount      = parseFloat(document.getElementById('expense-amount').value);
    const description = document.getElementById('expense-desc').value.trim();
    if (!paidById || !groupId || !amount || !description) return toast('Fill all fields', 'error');
    const r = await api('POST', '/expenses/add', { paidById, groupId, amount, description });
    setRes('expense-res', r);
    if (r.ok) toast('Expense added & split ✓');
    else toast('Failed to add expense', 'error');
}

// ════ TRANSACTIONS ════
async function settlePayment() {
    const splitId      = parseInt(document.getElementById('settle-split-id').value);
    const paidByUserId = parseInt(document.getElementById('settle-user-id').value);
    if (!splitId || !paidByUserId) return toast('Fill split ID and user ID', 'error');
    const r = await api('POST', '/transactions/settle', { splitId, paidByUserId });
    setRes('settle-res', r);
    if (r.ok) toast('Payment settled ✓');
    else toast('Settlement failed', 'error');
}

async function getUserTransactions() {
    const uid = document.getElementById('tx-user-id').value;
    if (!uid) return toast('Enter a user ID', 'error');
    const r  = await api('GET', '/transactions/user/' + uid);
    const el = document.getElementById('tx-list');
    if (!r.ok) { el.innerHTML = `<div class="response-box error">${JSON.stringify(r.data, null, 2)}</div>`; return; }
    if (!Array.isArray(r.data) || r.data.length === 0) {
        el.innerHTML = '<div class="empty-state"><div class="empty-icon">📭</div><p>No transactions found.</p></div>';
        return;
    }
    el.innerHTML = `<div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Amount</th><th>Description</th><th>Status</th></tr></thead>
        <tbody>${r.data.map(t => `<tr>
            <td><span class="badge badge-blue">#${t.id || '—'}</span></td>
            <td style="color:var(--accent);font-weight:500;">₹${t.amount ?? '—'}</td>
            <td>${t.description || '—'}</td>
            <td><span class="badge ${t.status === 'PAID' ? 'badge-green' : 'badge-amber'}">${t.status || '—'}</span></td>
        </tr>`).join('')}</tbody>
    </table></div>`;
}

// ════ BALANCE ════
async function getBalance() {
    const uid = document.getElementById('balance-user-id').value;
    if (!uid) return toast('Enter a user ID', 'error');
    const r = await api('GET', '/users/' + uid + '/balance');
    if (!r.ok) { toast('Failed to fetch balance', 'error'); return; }
    const d = r.data;
    document.getElementById('bal-owe').textContent  = '₹' + (d.totalYouOwe ?? 0).toFixed(2);
    document.getElementById('bal-owed').textContent = '₹' + (d.totalYouAreOwed ?? 0).toFixed(2);
    const net   = d.netBalance ?? 0;
    const netEl = document.getElementById('bal-net');
    netEl.textContent = (net >= 0 ? '+' : '') + '₹' + net.toFixed(2);
    netEl.className   = 'stat-value ' + (net > 0 ? 'positive' : net < 0 ? 'negative' : 'neutral');

    const breakdown = d.breakdown || {};
    const bEl       = document.getElementById('balance-breakdown');
    const entries   = Object.entries(breakdown);
    if (entries.length === 0) {
        bEl.innerHTML = '<p style="color:var(--text3);font-size:13px;">No breakdown data.</p>';
    } else {
        bEl.innerHTML = `<div class="table-wrap"><table>
            <thead><tr><th>Person</th><th>Amount</th></tr></thead>
            <tbody>${entries.map(([k,v]) => `<tr>
                <td style="color:var(--text);">${k}</td>
                <td style="color:${v >= 0 ? 'var(--accent)' : 'var(--danger)'};font-weight:500;">
                    ${v >= 0 ? '+' : ''}₹${parseFloat(v).toFixed(2)}
                </td>
            </tr>`).join('')}</tbody>
        </table></div>`;
    }
    document.getElementById('balance-result').style.display = 'block';
    toast('Balance loaded ✓');
}

// ════ NET SETTLEMENT ════
// Calls POST /users/net-settle — auto-cancels overlapping splits in DB
async function getNetSettlement() {
    const userAId = document.getElementById('settle-user-a').value.trim();
    const userBId = document.getElementById('settle-user-b').value.trim();
    if (!userAId || !userBId) return toast('Enter both user IDs', 'error');

    const r = await api('POST', `/users/net-settle?userAId=${userAId}&userBId=${userBId}`);
    if (!r.ok) { toast('Failed to calculate settlement', 'error'); return; }

    const d         = r.data;
    const net       = parseFloat(d.netAmount ?? 0);
    const direction = d.direction ?? '—';

    // Apply colour theme to result card
    const card = document.getElementById('net-result-card');
    card.className = 'net-result-card';
    if (direction.toLowerCase().includes('settled')) {
        card.classList.add('settled');
        document.getElementById('net-direction').style.color  = 'var(--accent2)';
        document.getElementById('net-amount-big').style.color = 'var(--accent2)';
    } else if (direction.toLowerCase().startsWith((d.fromUser || '').toLowerCase())) {
        card.classList.add('pays');
        document.getElementById('net-direction').style.color  = 'var(--danger)';
        document.getElementById('net-amount-big').style.color = 'var(--danger)';
    } else {
        card.classList.add('receives');
        document.getElementById('net-direction').style.color  = '#059669';
        document.getElementById('net-amount-big').style.color = '#059669';
    }

    document.getElementById('net-direction').textContent  = direction;
    document.getElementById('net-amount-big').textContent = direction.toLowerCase().includes('settled') ? '₹0' : '₹' + net.toFixed(2);

    // Gross breakdown rows
    document.getElementById('gross-owe-label').textContent   = (d.fromUser ?? '?') + ' owes ' + (d.toUser ?? '?');
    document.getElementById('gross-owe-amount').textContent  = '₹' + parseFloat(d.grossOwed ?? 0).toFixed(2);
    document.getElementById('gross-owed-label').textContent  = (d.toUser ?? '?') + ' owes ' + (d.fromUser ?? '?');
    document.getElementById('gross-owed-amount').textContent = '₹' + parseFloat(d.grossOwedBack ?? 0).toFixed(2);

    document.getElementById('settlement-raw-res').textContent = JSON.stringify(d, null, 2);
    document.getElementById('gross-details').classList.remove('open');
    document.getElementById('gross-toggle-btn').textContent = '▸ Show gross breakdown';
    document.getElementById('settlement-result').style.display = 'block';
    toast('Debts auto-cancelled & net calculated ✓');
}

let grossOpen = false;
function toggleGrossDetails() {
    grossOpen = !grossOpen;
    document.getElementById('gross-details').classList.toggle('open', grossOpen);
    document.getElementById('gross-toggle-btn').textContent = grossOpen
        ? '▾ Hide gross breakdown'
        : '▸ Show gross breakdown';
}

// ════ FRIENDS ════
async function addFriend() {
    const uid = document.getElementById('friend-user-id').value;
    const fid = document.getElementById('friend-friend-id').value;
    if (!uid || !fid) return toast('Enter both user IDs', 'error');
    const r = await api('POST', `/users/${uid}/add-friend/${fid}`);
    setRes('friend-res', r);
    if (r.ok) toast('Friend added ✓');
    else toast('Failed to add friend', 'error');
}

// ════ DASHBOARD ════
async function loadDashboardUsers() {
    const el = document.getElementById('dashboard-users');
    el.innerHTML = '<div class="empty-state"><div class="spinner"></div></div>';
    const r = await api('GET', '/users');
    if (!r.ok || !Array.isArray(r.data) || r.data.length === 0) {
        el.innerHTML = '<div class="empty-state"><div class="empty-icon">👤</div><p>No users yet. <a href="#" onclick="showSection(\'users\')" style="color:var(--accent);">Create one →</a></p></div>';
        return;
    }
    el.innerHTML = `<div class="table-wrap"><table>
        <thead><tr><th>ID</th><th>Name</th><th>Email</th></tr></thead>
        <tbody>${r.data.slice(0, 6).map(u => `<tr>
            <td><span class="badge badge-blue">#${u.id}</span></td>
            <td style="color:var(--text);font-weight:500;">${u.name}</td>
            <td style="font-size:12px;">${u.email}</td>
        </tr>`).join('')}</tbody>
    </table></div>`;
}

// Init
loadDashboardUsers();
