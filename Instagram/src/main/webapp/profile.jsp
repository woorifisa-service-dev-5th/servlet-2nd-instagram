<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// 프로필 페이지 주인 ID (URL 파라미터로 받거나 기본값 2)
String userIdParam = request.getParameter("user_id");
int profileUserId = (userIdParam != null) ? Integer.parseInt(userIdParam) : 2;

// 현재 사용자는 항상 1 (민영)
int currentUserId = 1;

// 더미 게시물 데이터
String profileImage = "https://picsum.photos/40/40?random=2";
String postImage = "https://picsum.photos/600/600?random=10";
String postContent = "오늘 카페에서 찍은 라떼아트 ☕️ 너무 예뻐서 마시기가 아까웠어요! #라떼아트 #카페 #일상";
int likeCount = 42;
int commentCount = 8;
String timeAgo = "3시간 전";
int postId = 1;
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>팔로팔로미</title>
</head>
<body>
    <div class="container">
        <!-- 게시물 헤더 -->
        <div class="post-header">
            <img src="<%= profileImage %>" alt="프로필 사진" class="profile-pic">
            <div class="user-info">
            </div>
            
            <!-- 팔로우 섹션 -->
            <div class="follow-section">
                <% if(currentUserId != profileUserId) { %>
                    <button id="followBtn" onclick="doFollow(<%= profileUserId %>)">팔로우</button>
                    <button id="unfollowBtn" onclick="doUnfollow(<%= profileUserId %>)" style="display:none;">언팔로우</button>
                <% } %>
                <div class="follow-counts">
                    팔로워: <span id="followersCount">0</span> | 팔로잉: <span id="followingsCount">0</span>
                </div>
            </div>
        </div>

        <!-- 게시물 이미지 -->
        <img src="<%= postImage %>" alt="게시물 이미지" class="post-image">

        <!-- 게시물 액션 버튼 -->
        <div class="post-actions">
            <button class="action-btn like-btn" onclick="toggleLike(<%= postId %>)">
                ❤️ 좋아요
            </button>
            <button class="action-btn comment-btn" onclick="goToComments(<%= postId %>)">
                💬 댓글
            </button>
            
        </div>

        <!-- 좋아요 수 -->
        <div class="post-stats">
            <span class="likes-count">좋아요 <%= likeCount %>개</span>
        </div>

        <!-- 게시물 내용 -->
        <div class="post-caption">
            <%= postContent %>
        </div>

        <!-- 댓글 보기 링크 -->
        <a href="Comments.jsp?post_id=<%= postId %>" class="view-comments">
            댓글 <%= commentCount %>개 모두 보기
        </a>

        <!-- 게시 시간 -->
        <div class="post-time"><%= timeAgo %></div>
    </div>

    <script>
        // 페이지 로드 시 팔로우 상태 확인
        window.onload = function() {
            checkFollowStatus();
        }

        // 팔로우 상태 확인
        function checkFollowStatus() {
            fetch('/Instagram/follow?receiver_id=<%= profileUserId %>')
            .then(response => response.json())
            .then(data => {
                // 카운트 업데이트
                document.getElementById('followersCount').textContent = data.followersCount;
                document.getElementById('followingsCount').textContent = data.followingsCount;
                
                // 팔로우 버튼 상태 업데이트
                if(data.isFollowing) {
                    document.getElementById('followBtn').style.display = 'none';
                    document.getElementById('unfollowBtn').style.display = 'inline-block';
                } else {
                    document.getElementById('followBtn').style.display = 'inline-block';
                    document.getElementById('unfollowBtn').style.display = 'none';
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        }

        // 팔로우
        function doFollow(targetUserId) {
            fetch('/Instagram/follow', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'receiver_id=' + targetUserId + '&action=follow'
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                // 상태 다시 확인
                checkFollowStatus();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('요청 실패');
            });
        }

        // 언팔로우
        function doUnfollow(targetUserId) {
            fetch('/Instagram/follow', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'receiver_id=' + targetUserId + '&action=unfollow'
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                // 상태 다시 확인
                checkFollowStatus();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('요청 실패');
            });
        }

        // 좋아요 토글
        function toggleLike(postId) {
            fetch('/Instagram/like', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'post_id=' + postId
            })
            .then(response => response.json())
            .then(data => {
                alert(data.message);
                // 좋아요 수 업데이트 등 필요시 추가
                location.reload(); // 간단히 페이지 새로고침
            })
            .catch(error => {
                console.error('Error:', error);
                alert('요청 실패');
            });
        }

        // 댓글 페이지로 이동
        function goToComments(postId) {
            location.href = 'Comments.jsp?post_id=' + postId;
        }
    </script>
</body>
</html>