<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>댓글 테스트</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<h2>댓글 목록</h2>
<div id="commentList"></div>

<h3>댓글 작성</h3>
<form id="commentForm" action="comment" method="post">
    <input type="hidden" name="postId" value="1" /> <!-- 임시 게시글 ID -->
    <input type="hidden" name="memberId" value="1001" /> <!-- 임시 사용자 ID -->
    <textarea name="content" placeholder="댓글을 입력하세요"></textarea><br>
    <button type="submit">댓글 달기</button>
</form>

<script>
    function loadComments() {
        $.getJSON("<%=request.getContextPath()%>/comment?postId=1", function(data) {
            let html = "";
            data.forEach(c => {
                html += "<p><b>" + c.memberId + ":</b> " + c.content + "</p>";
            });
            $("#commentList").html(html);
        });
    }

    $(document).ready(function() {
        loadComments();

        $("#commentForm").submit(function(e) {
            e.preventDefault();
            $.post($(this).attr("action"), $(this).serialize(), function() {
                loadComments();
            });
        });
    });
</script>
</body>
</html>
