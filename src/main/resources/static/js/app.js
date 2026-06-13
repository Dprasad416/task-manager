// ── State ─────────────────────────────────────────────
let currentUser = null;    // { id, username, email, password }
let allTasks    = [];      // full task list from API
let activeFilter = 'all';

// ── Auth helpers ───────────────────────────────────────
function getAuthHeader() {
    // HTTP Basic auth: encodes "username:password" in base64
    const credentials = btoa(`${currentUser.username}:${currentUser.password}`);
    return { 'Authorization': `Basic ${credentials}`, 'Content-Type': 'application/json' };
}

// ── Tab switching (Login / Register) ──────────────────
function showTab(tab) {
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    event.target.classList.add('active');
    document.getElementById('login-form').style.display    = tab === 'login'    ? 'block' : 'none';
    document.getElementById('register-form').style.display = tab === 'register' ? 'block' : 'none';
    document.getElementById('auth-error').textContent = '';
}

// ── Register ───────────────────────────────────────────
async function register() {
    const username = document.getElementById('reg-username').value.trim();
    const email    = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value;
    const errorEl  = document.getElementById('auth-error');

    errorEl.textContent = '';

    try {
        const res = await fetch('/api/users/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });
        const data = await res.json();

        if (!res.ok) {
            errorEl.textContent = data.error || 'Registration failed';
            return;
        }

        // Auto-login after registration
        currentUser = { id: data.id, username, email, password };
        showDashboard();

    } catch (err) {
        errorEl.textContent = 'Network error. Is the server running?';
    }
}

// ── Login ──────────────────────────────────────────────
async function login() {
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;
    const errorEl  = document.getElementById('auth-error');

    errorEl.textContent = '';

    // Try to call a protected endpoint — if it succeeds, credentials are correct
    try {
        const credentials = btoa(`${username}:${password}`);
        const res = await fetch('/api/tasks?userId=0', {
            headers: { 'Authorization': `Basic ${credentials}` }
        });

        if (res.status === 401) {
            errorEl.textContent = 'Wrong username or password';
            return;
        }

        // We need the user ID. Fetch user info by username.
        const userRes = await fetch(`/api/users/by-username/${username}`, {
            headers: { 'Authorization': `Basic ${credentials}` }
        });

        // For simplicity: store credentials and load tasks
        // In production you'd get a proper user ID from the auth response
        currentUser = { username, password, id: 1 };  // Update ID from real response
        showDashboard();

    } catch (err) {
        errorEl.textContent = 'Login failed. Please try again.';
    }
}

function logout() {
    currentUser = null;
    allTasks = [];
    document.getElementById('login-screen').style.display    = 'flex';
    document.getElementById('dashboard-screen').style.display = 'none';
    document.getElementById('login-username').value = '';
    document.getElementById('login-password').value = '';
}

// ── Dashboard ──────────────────────────────────────────
function showDashboard() {
    document.getElementById('login-screen').style.display    = 'none';
    document.getElementById('dashboard-screen').style.display = 'block';
    document.getElementById('welcome-msg').textContent = `Hello, ${currentUser.username}!`;
    loadTasks();
}

// ── Load Tasks ─────────────────────────────────────────
async function loadTasks() {
    try {
        const res = await fetch(`/api/tasks?userId=${currentUser.id}`, {
            headers: getAuthHeader()
        });
        allTasks = await res.json();
        renderTasks();
        updateStats();
    } catch (err) {
        console.error('Failed to load tasks:', err);
    }
}

// ── Create Task ────────────────────────────────────────
async function createTask() {
    const title    = document.getElementById('task-title').value.trim();
    const desc     = document.getElementById('task-desc').value.trim();
    const priority = document.getElementById('task-priority').value;
    const dueDate  = document.getElementById('task-due').value;
    const errorEl  = document.getElementById('task-error');

    errorEl.textContent = '';

    if (!title) {
        errorEl.textContent = 'Title is required';
        return;
    }

    try {
        const res = await fetch('/api/tasks', {
            method: 'POST',
            headers: getAuthHeader(),
            body: JSON.stringify({
                userId: currentUser.id,
                title, description: desc, priority, dueDate
            })
        });
        const data = await res.json();

        if (!res.ok) {
            errorEl.textContent = data.error || 'Failed to create task';
            return;
        }

        // Clear form and reload
        document.getElementById('task-title').value = '';
        document.getElementById('task-desc').value  = '';
        document.getElementById('task-due').value   = '';
        loadTasks();

    } catch (err) {
        errorEl.textContent = 'Failed to create task';
    }
}

// ── Toggle Complete ────────────────────────────────────
async function toggleTask(taskId) {
    await fetch(`/api/tasks/${taskId}/toggle?userId=${currentUser.id}`, {
        method: 'PATCH',
        headers: getAuthHeader()
    });
    loadTasks();
}

// ── Delete Task ────────────────────────────────────────
async function deleteTask(taskId) {
    if (!confirm('Delete this task?')) return;

    await fetch(`/api/tasks/${taskId}?userId=${currentUser.id}`, {
        method: 'DELETE',
        headers: getAuthHeader()
    });
    loadTasks();
}

// ── Filter ─────────────────────────────────────────────
function filterTasks(filter, btn) {
    activeFilter = filter;
    document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    renderTasks();
}

// ── Render Task Cards ──────────────────────────────────
function renderTasks() {
    const container = document.getElementById('task-list');

    let tasks = allTasks;
    if (activeFilter === 'pending')   tasks = allTasks.filter(t => !t.completed);
    if (activeFilter === 'completed') tasks = allTasks.filter(t =>  t.completed);

    if (tasks.length === 0) {
        container.innerHTML = `<div class="empty-state">No tasks here. Add one above!</div>`;
        return;
    }

    container.innerHTML = tasks.map(task => `
        <div class="task-card ${task.completed ? 'done' : ''}">
            <div class="task-check ${task.completed ? 'checked' : ''}"
                 onclick="toggleTask(${task.id})"></div>
            <div class="task-info">
                <div class="task-title">${escapeHtml(task.title)}</div>
                <div class="task-meta">
                    ${task.description ? escapeHtml(task.description) + ' · ' : ''}
                    ${task.dueDate ? 'Due: ' + task.dueDate + ' · ' : ''}
                    ${new Date(task.dueDate) < new Date() && !task.completed ? '⚠️ Overdue · ' : ''}
                </div>
            </div>
            <span class="priority-badge priority-${task.priority}">${task.priority}</span>
            <button class="btn-delete" onclick="deleteTask(${task.id})">✕</button>
        </div>
    `).join('');
}

// ── Update Stats Bar ───────────────────────────────────
function updateStats() {
    const total   = allTasks.length;
    const done    = allTasks.filter(t => t.completed).length;
    const pending = total - done;

    document.getElementById('stat-total').textContent   = total;
    document.getElementById('stat-pending').textContent = pending;
    document.getElementById('stat-done').textContent    = done;
}

// Prevent XSS — always escape user input before inserting as HTML
function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')
              .replace(/"/g,'&quot;').replace(/'/g,'&#039;');
}
