drop table member cascade constraints purge;
--1. index.jsp에서 시작 합니다.
--2. 관리자 계정 admin, 비번 1234를 만듭니다.
--3. 사용자 계정을 3개 만듭니다.

CREATE TABLE member(
	id 			VARCHAR2(12),  
	password 	VARCHAR2(10), 
	name 		VARCHAR2(15),  
	age 		number(2),  
	gender 		VARCHAR2(6),  
	email 		VARCHAR2(30),   
	memberfile 	VARCHAR2(50),   
	PRIMARY KEY(id)
);

--memberfile은 회원 정보 수정시 적용합니다.