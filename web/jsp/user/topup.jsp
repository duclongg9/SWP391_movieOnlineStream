<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Top Up</title>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body id="top">
  <main>
    <section class="auth-section">
      <div class="container">
        <h2 class="h2 section-title">Top Up</h2>
        <form id="topupForm" class="auth-form">
          <input type="number" name="amount" placeholder="Amount (VND)" required />
          <button type="submit" class="btn btn-primary">Submit</button>
        </form>
        <div style="margin-top:20px;">
          <p>Scan QR to pay:</p>
          <img src="<%=request.getContextPath()%>/assets/images/logo.svg" alt="QR" style="width:200px;height:200px;"/>
        </div>
        <pre id="result"></pre>
      </div>
    </section>
  </main>
<script>
const base = '<%=request.getContextPath()%>';
const token = localStorage.getItem('token');

document.getElementById('topupForm').addEventListener('submit', async function(e){
  e.preventDefault();
  const data = new URLSearchParams(new FormData(e.target));
  const res = await fetch(base + '/api/payment/topup', {method:'POST', body:data, headers:{Authorization:'Bearer '+token}});
  document.getElementById('result').textContent = await res.text();
});
</script>
</body>
</html>
