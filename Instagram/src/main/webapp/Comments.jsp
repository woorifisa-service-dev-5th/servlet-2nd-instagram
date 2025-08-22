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
const contextPath = "<%= request.getContextPath() %>";
const postId = 1;
function loadComments() {
	
    $.getJSON(contextPath + "/comment?postId=" + postId, function(data) {
        let html = "";
        data.forEach(c => {
        	console.log('c', c);
        	console.log(c.commentId);
            html += "<p><b>" + c.memberId + ":</b> " + c.content +
                " ❤️ <span id='like-count-" + c.commentId + "'>" + c.likeCount + "</span>" +
                " <button onclick='likeComment(" + c.commentId + ")'>좋아요</button></p>";
        });
        $("#commentList").html(html);
    });
}

function likeComment(commentId) {
    console.log("Sending like for commentId:", commentId);
    $.ajax({
        url: contextPath + "/like",
        method: "POST",
        data: { commentId: commentId , postId: postId },
        success: function(res) {
            $("#like-count-" + commentId).text(res.likeCount);
        },
        error: function(xhr, status, error) {
            alert("좋아요 처리 중 오류가 발생했습니다: " + error);
        }
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
