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
        <div class="mb-3">
        <a href="<%=request.getContextPath()%>/movies" class="btn btn-secondary">&larr; Back Home</a>
      </div>
      <div class="profile-header">
        <img id="profileImg" src="${avatarUrl}" alt="Profile" class="profile-img">        
        <h2 id="fullName" class="mb-1">Loading...</h2>
        <p id="emailDisplay" class="text-muted mb-0">Email: Loading...</p>
        <p id="accountType" class="text-muted">Account: Loading...</p>
      </div>

      <!-- Personal Information Card -->
      <div class="card">
        <div class="card-body">
          <h5 class="card-title mb-4">Personal Information</h5>
          <div class="mb-3">
            <label class="form-label">Phone</label>
            <div id="phoneView">
              <span id="phoneDisplay"></span>
              <button type="button" id="editPhoneBtn" class="btn btn-sm btn-outline-primary ms-2">Edit</button>
            </div>
            <form id="phoneForm" class="d-none mt-2">
              <div class="input-group">
                <input type="text" class="form-control" id="phoneInput" placeholder="Enter your phone number" required />
                <button type="submit" class="btn btn-primary">Save</button>
                <button type="button" id="cancelPhoneBtn" class="btn btn-secondary">Cancel</button>
              </div>
            </form>
          </div>
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
if (!token) {
  window.location.href = base + '/login';
}
document.addEventListener('DOMContentLoaded', function() {
  const fullName = document.getElementById('fullName');
  const emailDisplay = document.getElementById('emailDisplay');
  const accountType = document.getElementById('accountType');
  const phoneDisplay = document.getElementById('phoneDisplay');
  const phoneForm = document.getElementById('phoneForm');
  const phoneInput = document.getElementById('phoneInput');
  const phoneView = document.getElementById('phoneView');
  const editPhoneBtn = document.getElementById('editPhoneBtn');
  const cancelPhoneBtn = document.getElementById('cancelPhoneBtn');
  const profileImg = document.getElementById('profileImg');
  const walletBalance = document.getElementById('walletBalance');
  const points = document.getElementById('points');
  const cards = document.getElementById('cards');
  const packagesBody = document.getElementById('packagesBody');
  const noPackages = document.getElementById('noPackages');
  const packagesTable = document.getElementById('packagesTable');
  const resultDiv = document.getElementById('result');
  const loading = document.getElementById('loading');

  const storedPic = localStorage.getItem('picture');
  if (profileImg && storedPic) profileImg.src = storedPic;

// Fetch profile data
function handleUnauthorized(r){
  if(r.status === 401){
    localStorage.removeItem('token');
    localStorage.removeItem('picture');
    window.location.href = base + '/login';
    return true;
  }
  return false;
}


fetch(base + '/api/user/profile', {
  headers: {Authorization: 'Bearer ' + token},
  credentials: 'include'
})
  .then(async r => {
    if (handleUnauthorized(r)) throw new Error('Unauthorized');
    if (!r.ok) {
      const msg = await r.json().catch(() => ({error:'Failed to fetch profile'}));
      throw new Error(msg.error || 'Failed to fetch profile');
    }
    return r.json();
  })
  .then(d => {
    fullName.textContent = d.fullName || 'N/A';
    emailDisplay.textContent = 'Email: ' + (d.email || 'N/A');
    accountType.textContent = 'Account: ' + (d.role === 'ADMIN' ? 'Admin' : 'Customer');
    phoneDisplay.textContent = d.phone || 'Not set';
    walletBalance.textContent = d.walletBalance ? d.walletBalance.toLocaleString() + ' VND' : '0 VND';
    points.textContent = d.points || '0';
    cards.textContent = d.cards ? d.cards.join(', ') : 'No cards added.';
    if(profileImg && d.picture) profileImg.src = d.picture;
    if(d.picture) localStorage.setItem('picture', d.picture);
  })
  .catch(err => {
    resultDiv.textContent = 'Error: ' + err.message;
    resultDiv.className = 'result error';
    resultDiv.style.display = 'block';
    fullName.textContent = 'N/A';
    emailDisplay.textContent = 'Email: N/A';
    accountType.textContent = 'Account: N/A';
    phoneDisplay.textContent = 'N/A';
    walletBalance.textContent = '0 VND';
    points.textContent = '0';
    cards.textContent = 'No cards added.';
  });

// Fetch purchase history for packages
fetch(base + '/api/purchase/history', {headers: {Authorization: 'Bearer ' + token}})
  .then(async r => {
    if (handleUnauthorized(r)) throw new Error('Unauthorized');
    if (!r.ok) {
      const msg = await r.json().catch(() => ({error:'Failed to fetch history'}));
      throw new Error(msg.error || 'Failed to fetch history');
    }
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
      tr.innerHTML =
        '<td>' + (p.packageName || 'N/A') + '</td>' +
        '<td>' + (p.purchaseDate ? new Date(p.purchaseDate).toLocaleDateString() : 'N/A') + '</td>' +
        '<td>' + (p.expirationDate ? new Date(p.expirationDate).toLocaleDateString() : 'N/A') + '</td>' +
        '<td>' + (p.status || 'Active') + '</td>';
      packagesBody.appendChild(tr);
    });
  })
  .catch(err => {
    const msg = 'Error loading packages: ' + err.message;
    noPackages.textContent = msg;
    noPackages.style.display = 'block';
    packagesTable.style.display = 'none';
    resultDiv.textContent = msg;
    resultDiv.className = 'result error';
    resultDiv.style.display = 'block';
  });

editPhoneBtn.addEventListener('click', () => {
  phoneInput.value = phoneDisplay.textContent !== 'Not set' ? phoneDisplay.textContent : '';
  phoneView.style.display = 'none';
  phoneForm.classList.remove('d-none');
});

cancelPhoneBtn.addEventListener('click', () => {
  phoneForm.classList.add('d-none');
  phoneView.style.display = 'block';
});

phoneForm.addEventListener('submit', async function(e){
  e.preventDefault();
  if(!confirm('Are you sure you want to change your phone number?')) return;
  const data = new URLSearchParams();
  data.append('phone', phoneInput.value);
  resultDiv.style.display = 'none';
  loading.style.display = 'block';
  try {
    const res = await fetch(base + '/api/user/profile', {
      method: 'PUT',
      body: data,
      headers: { Authorization: 'Bearer ' + token },
      credentials: 'include'
    });
    const resp = await res.json().catch(() => null);
    loading.style.display = 'none';
    if(res.ok && resp && resp.phone){
      phoneDisplay.textContent = resp.phone;
      phoneForm.classList.add('d-none');
      phoneView.style.display = 'block';
      resultDiv.textContent = 'Updated successfully';
      resultDiv.className = 'result success';
    } else {
      resultDiv.textContent = (resp && resp.error) || 'Update failed';
      resultDiv.className = 'result error';
    }
    resultDiv.style.display = 'block';
  } catch (err) {
    loading.style.display = 'none';
    resultDiv.textContent = 'Error: ' + err.message;
    resultDiv.className = 'result error';
    resultDiv.style.display = 'block';
  }
  
  });
});
</script>
</body>

</html>