<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>

<body>
  <c:forEach var="cmt" items="${comments}">
      <div>
          <p>${cmt.content} - ❤️ 
              <span id="like-count-${cmt.commentId}">${cmt.likeCount}</span>
          </p>
          <button onclick="likeComment(${cmt.commentId})">❤️ 좋아요</button>
      </div>
  </c:forEach>

  <script>
  function likeComment(commentId) {
      fetch('/comment-like?commentId=' + commentId, {
          method: 'POST'
      })
      .then(res => res.json())
      .then(data => {
          document.getElementById("like-count-" + commentId).textContent = data.likeCount;
      });
  }
  </script>
</body>

