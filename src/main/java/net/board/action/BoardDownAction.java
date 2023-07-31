package net.board.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.member.action.Action;
import net.member.action.ActionForward;

public class BoardDownAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String fileName = request.getParameter("filename");
		System.out.println("filename = " + fileName);
		
		String savePath = "boardupload";
		
		//서블릿의 실행 환경 정보를 담고 있는 객체를 리턴합니다.
		ServletContext context = request.getServletContext();
		String sDownloadPath = context.getRealPath(savePath);
		
		 String sFilePath = sDownloadPath + File.separator + fileName; // 변수 이름을 수정하였습니다.

		    System.out.println(sFilePath);

		    byte b[] = new byte[4096];

		    // sFilePath에 있는 파일의 MimeType을 가져옵니다.
		    String sMimeType = context.getMimeType(sFilePath);
		    System.out.println("sMimeType >>> " + sMimeType);

		    if (sMimeType == null) // 변수 이름을 수정하였습니다.
		        sMimeType = "application/octet-stream"; // 빠진 세미콜론을 추가하였습니다.

		    response.setContentType(sMimeType);
		    
		    // - 이 부분이 한글 파일명이 깨지는 것을 방지해 줍니다.
		    String sEncoding = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
		    System.out.println(sEncoding);
		    
		    
//		     Content-Disposition: attachment: 브라우저는 해당 Content를
//		     처리하지 않고, 다운로드하게 됩니다.
		     response.setHeader("Content-Disposition", "attachment; filename="+ sEncoding);
		    
		    
		    try(
		    	//웹 브라우저로의 출력 스트림 생성합니다.
		    	BufferedOutputStream out2 = new BufferedOutputStream(response.getOutputStream());
		    		
		    	//sFilePath로 지정한 파일에 대한 입력 스트림을 생성합니다.
		    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(sFilePath));	
		    	)
		    
		    {
		    	int numRead;
		    	//read(b, 0m b.length) : 바이트 배열 b의 0변 부터 b.length 크기 만큼 읽어옵니다.
		    	while ((numRead = in.read(b, 0, b.length)) != -1 ) { //읽을 데이터가 존재하는 경우
		    		//바이트 배열 b의 0변 부터 numRead크기 만큼 브라우저로 출력
		    		out2.write(b, 0, numRead);
		    		
		    	}
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    return null;
		}

}
