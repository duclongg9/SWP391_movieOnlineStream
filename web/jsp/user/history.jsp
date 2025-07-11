<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Purchase History</title>
  <link rel="shortcut icon" href="<%=request.getContextPath()%>/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body id="top">
<main>
  <section class="auth-section">
    <div class="container">
      <h2 class="h2 section-title">Purchase History</h2>
      <ul id="historyList"></ul>
    </div>
  </section>
</main>
<script>
const base = '<%=request.getContextPath()%>';
const token = localStorage.getItem('token');
fetch(base + '/api/purchase/history', {headers:{Authorization:'Bearer '+token}})
  .then(r=>r.json()).then(list => {
    const ul = document.getElementById('historyList');
    list.forEach(p => {
      const li = document.createElement('li');
      li.textContent = JSON.stringify(p);
      ul.appendChild(li);
    });
  });
</script>
</body>
</html>