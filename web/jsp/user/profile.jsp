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
</head>
<body id="top">
<main>
  <section class="auth-section">
    <div class="container">
      <h2 class="h2 section-title">Profile</h2>
      <div style="margin-bottom:15px; color:#fff;" id="fullName"></div>
      <div style="margin-bottom:15px; color:#fff;" id="emailDisplay"></div>
      <form id="profileForm" class="auth-form">
        <input type="text" name="phone" id="phoneField" placeholder="Phone" required />
        <button type="submit" class="btn btn-primary">Update</button>
      </form>
      <form id="passwordForm" class="auth-form">
        <input type="password" name="oldPassword" placeholder="Old Password" required />
        <input type="password" name="newPassword" placeholder="New Password" required />
        <button type="submit" class="btn btn-primary">Change Password</button>
      </form>
      <pre id="result"></pre>
    </div>
  </section>
</main>
<script>
const base = '<%=request.getContextPath()%>';
const token = localStorage.getItem('token');
fetch(base + '/api/user/profile', {headers: {Authorization: 'Bearer ' + token}})
  .then(r => r.json())
  .then(d => {
    document.getElementById('fullName').textContent = 'Name: ' + d.fullName;
    document.getElementById('emailDisplay').textContent = 'Email: ' + d.email;
    document.getElementById('phoneField').value = d.phone || '';
  });

document.getElementById('profileForm').addEventListener('submit', async function(e){
  e.preventDefault();
  const data = new URLSearchParams(new FormData(e.target));
  const res = await fetch(base + '/api/user/profile', {
    method:'PUT',
    body:data,
    headers:{Authorization:'Bearer '+token}
  });
  document.getElementById('result').textContent = await res.text();
});

document.getElementById('passwordForm').addEventListener('submit', async function(e){
  e.preventDefault();
  const data = new URLSearchParams(new FormData(e.target));
  const res = await fetch(base + '/api/user/change-password', {method:'POST', body:data, headers:{Authorization:'Bearer '+token}});
  document.getElementById('result').textContent = await res.text();
});
</script>
</body>
</html>