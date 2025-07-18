<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Profile</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <!-- Custom CSS for modern look -->
  <style>
    body {
      background-color: #f8f9fa;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }
    .profile-container {
      max-width: 800px;
      margin: 50px auto;
    }
    .profile-header {
      background-color: #fff;
      border-radius: 10px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      padding: 2rem;
      text-align: center;
      margin-bottom: 2rem;
    }
    .profile-img {
      width: 150px;
      height: 150px;
      object-fit: cover;
      border-radius: 50%;
      border: 3px solid #007bff;
      margin-bottom: 1rem;
    }
    .card {
      border: none;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
      border-radius: 10px;
      margin-bottom: 2rem;
    }
    .card-title {
      font-weight: 600;
      color: #343a40;
    }
    .form-label {
      font-weight: 500;
      color: #495057;
    }
    .btn-primary {
      background-color: #007bff;
      border-color: #007bff;
      transition: all 0.3s ease;
    }
    .btn-primary:hover {
      background-color: #0056b3;
      border-color: #0056b3;
    }
    .info-section {
      background-color: #f1f1f1;
      padding: 1.5rem;
      border-radius: 8px;
    }
    .result {
      margin-top: 20px;
      padding: 10px;
      border-radius: 5px;
      text-align: center;
    }
    .success {
      background-color: #d4edda;
      color: #155724;
    }
    .error {
      background-color: #f8d7da;
      color: #721c24;
    }
    .loading {
      text-align: center;
      margin-top: 20px;
    }
    .balance-info {
      display: flex;
      justify-content: space-between;
      margin-bottom: 1rem;
    }
    .table {
      margin-top: 1rem;
    }
    @media (max-width: 768px) {
      .profile-img {
        width: 120px;
        height: 120px;
      }
      .profile-header {
        padding: 1.5rem;
      }
    }
  </style>
</head>
<body id="top">
<main>
  <section class="auth-section">
    <div class="container profile-container">
      <div class="profile-header">
        <img src="<%=request.getContextPath()%>/assets/images/avatar-placeholder.jpg" alt="Profile" class="profile-img"> <!-- Add a placeholder image -->
        <h2 id="fullName" class="mb-1">Loading...</h2>
        <p id="emailDisplay" class="text-muted mb-0">Email: Loading...</p>
      </div>

      <!-- Personal Information Card -->
      <div class="card">
        <div class="card-body">
          <h5 class="card-title mb-4">Personal Information</h5>
          <form id="profileForm">
            <div class="mb-3">
              <label for="phoneField" class="form-label">Phone</label>
              <input type="text" class="form-control" name="phone" id="phoneField" placeholder="Enter your phone number" required />
            </div>
            <button type="submit" class="btn btn-primary w-100">Update Phone</button>
          </form>
        </div>
      </div>

      <!-- Account Balance and Points -->
      <div class="card">
        <div class="card-body info-section">
          <h5 class="card-title mb-4">Account Details</h5>
          <div class="balance-info">
            <div>
              <strong>Wallet Balance (VND):</strong>
              <span id="walletBalance">Loading...</span>
            </div>
            <div>
              <strong>Points:</strong>
              <span id="points">Loading...</span>
            </div>
          </div>
          <!-- Placeholder for Cards -->
          <div>
            <strong>Payment Cards:</strong>
            <p id="cards">No cards added.</p> <!-- Assume API returns cards, or fetch separately -->
          </div>
        </div>
      </div>

      <!-- Purchased Packages -->
      <div class="card">
        <div class="card-body">
          <h5 class="card-title mb-4">Purchased Packages</h5>
          <table class="table table-striped table-hover" id="packagesTable">
            <thead>
              <tr>
                <th>Package Name</th>
                <th>Purchase Date</th>
                <th>Expiration Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody id="packagesBody"></tbody>
          </table>
          <div id="noPackages" style="display: none;">No purchased packages found.</div>
        </div>
      </div>

      <div id="result" class="result" style="display: none;"></div>
      <div id="loading" class="loading" style="display: none;">Processing...</div>
    </div>
  </section>
</main>
<script>
const base = '<%=request.getContextPath()%>';
const token = localStorage.getItem('token');
const fullName = document.getElementById('fullName');
const emailDisplay = document.getElementById('emailDisplay');
const phoneField = document.getElementById('phoneField');
const walletBalance = document.getElementById('walletBalance');
const points = document.getElementById('points');
const cards = document.getElementById('cards');
const packagesBody = document.getElementById('packagesBody');
const noPackages = document.getElementById('noPackages');
const packagesTable = document.getElementById('packagesTable');
const resultDiv = document.getElementById('result');
const loading = document.getElementById('loading');

// Fetch profile data
fetch(base + '/api/user/profile', {headers: {Authorization: 'Bearer ' + token}})
  .then(r => {
    if (!r.ok) throw new Error('Failed to fetch profile');
    return r.json();
  })
  .then(d => {
    fullName.textContent = d.fullName || 'N/A';
    emailDisplay.textContent = 'Email: ' + (d.email || 'N/A');
    phoneField.value = d.phone || '';
    walletBalance.textContent = d.walletBalance ? d.walletBalance.toLocaleString() + ' VND' : '0 VND'; // Assume API returns walletBalance
    points.textContent = d.points || '0'; // Assume API returns points
    cards.textContent = d.cards ? d.cards.join(', ') : 'No cards added.'; // Assume API returns cards array
  })
  .catch(err => {
    fullName.textContent = 'Error: ' + err.message;
  });

// Fetch purchase history for packages
fetch(base + '/api/purchase/history', {headers: {Authorization: 'Bearer ' + token}})
  .then(r => {
    if (!r.ok) throw new Error('Failed to fetch history');
    return r.json();
  })
  .then(list => {
    const packages = list.filter(p => p.type === 'package'); // Assume history items have 'type' == 'package'
    if (packages.length === 0) {
      noPackages.style.display = 'block';
      packagesTable.style.display = 'none';
      return;
    }
    packages.forEach(p => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${p.packageName || 'N/A'}</td>
        <td>${p.purchaseDate ? new Date(p.purchaseDate).toLocaleDateString() : 'N/A'}</td>
        <td>${p.expirationDate ? new Date(p.expirationDate).toLocaleDateString() : 'N/A'}</td>
        <td>${p.status || 'Active'}</td>
      `;
      packagesBody.appendChild(tr);
    });
  })
  .catch(err => {
    noPackages.textContent = 'Error loading packages: ' + err.message;
    noPackages.style.display = 'block';
    packagesTable.style.display = 'none';
  });

document.getElementById('profileForm').addEventListener('submit', async function(e){
  e.preventDefault();
  const data = new URLSearchParams(new FormData(e.target));
  resultDiv.style.display = 'none';
  loading.style.display = 'block';
  try {
    const res = await fetch(base + '/api/user/profile', {
      method: 'PUT',
      body: data,
      headers: { Authorization: 'Bearer ' + token }
    });
    const text = await res.text();
    loading.style.display = 'none';
    resultDiv.textContent = text;
    resultDiv.className = res.ok ? 'result success' : 'result error';
    resultDiv.style.display = 'block';
  } catch (err) {
    loading.style.display = 'none';
    resultDiv.textContent = 'Error: ' + err.message;
    resultDiv.className = 'result error';
    resultDiv.style.display = 'block';
  }
});
</script>
</body>
</html>