<%@ page language="java" contentType="text/html; charset=EUC-KR" 
		pageEncoding="EUC-KR"%>
		
<!DOCTYPE html>
<html>
<head>
<title>로그인 처리</title>
<link href="css/join.css" type="text/css" rel ="stylesheet">
<script src="js/jquery-3.7.0.js"></script>
<script>
	$(function() {
		let checkid = false;
		let checkemail = false;
		
		$("input[name = id]").on('keyup',
				function() {
			
			//[A-Za-z0-9_]의 의미가 \w
			const pattern = /^\w{5,12}$/;
			const id = $("input:eq(0)").val();
			if (!pattern.test(id)) {
				$("#message").css('color', 'red')
							 .html("영문자 숫자 _로 5 ~ 12자 가능합니다.");
				checkid = false;
				return;
			}
			
			$.ajax({
				url : "idcheck.net",
				 data: { id: id },  
		            success: function(resp) {
					if(resp == -1) {//db에 해당 id 가 없는 경우
						$("#message").css('color', 'green').html("사용 가능한 아이디 입니다.");
						checkid = true;
					} else { //db에 해당 id 가 있는 경우(0)
					$("#message").css('color', 'blue').html("사용중인 아이디 입니다.");
					checkid = false;
					}
				}
			}); //ajax end
		}); //keyup end
	
	$("input[name=email]").on('keyup',
			function() {
				//[A-Za-z0-9_]와 동일한 것이 \w 입니다.
				//+는 1회 이상 반복을 의미하고 {1,}와 동일합니다.
				//\w+ 는 [AZa-z0-9_]를 1개 이상 사용하라는 의미입니다.
				const pattern = /^\w+@\w+[.]\w{3}$/;
				const email_value = $(this).val();
				
				if(!pattern.test(email_value)) {
					$("#email_message").css('color', 'red')
									   .html("이메일형식이 맞지 않습니다.")
					checkemail = false;				   
				} else {
					$("#email_message").css('color', 'green')
									   .html("이메일형식에 맞습니다.")
					checkemail = true;				   
				}
	}); //email keyup 이벤트 처리 끝

	$('form').submit(function() {
		if (!$.isNumeric($("input[name='age']").val())) {
			alert("나이는 숫자를 입력하세요");
			$("input[name='age']").val('').focus();
			return false;
		}
		
		if(!checkid) {
			alert("사용가능한 id로 입력하세요.");
			$("input[name=id]").val('').focus();
			return false;
		}
		
		if(!checkemail){
			alert("email 형식을 확인하세요.");
			$("input[name=email]").focus();
			return false;
		}
	}); //submit
	});//ready
</script>
</head>
<body>
	<form name="joinform" action="joinProcess.net" method="post">

 <h1>회원가입 </h1>
 	 <hr>
 	 <b>아이디</b>
		<input type="text" name="id" placeholder="Enter id" maxLength="12" required>
		<span id="message"></span>
		
		<b>비밀번호</b>
		<input type="password" name="pass" placeholder="Enter password" required>
		
		<b>이름</b>
		<input type="text" name="name" placeholder="Enter name" maxLength="5" required>
		
		<b>나이</b>
		<input type="text" name="age"  maxLength="2" placeholder="Enter age" required>
		
		<b>성별</b>
		<div>
			<input type="radio" name="gender" value="남" checked><span>남자</span>
			<input type="radio" name="gender" value="여"><span>여자</span>
		</div>
		
		<b>이메일 주소</b>
			<input type="text" name="email" placeholder="Enter name" maxLength="30" required>
			<span id="email_message"></span>
			<div class="clearfix">
				<button type="submit" class="submitbtn">회원가입</button>
				<button type="reset" class="cancelbtn">다시작성</button>
			</div>
</form>
</body>
</html>