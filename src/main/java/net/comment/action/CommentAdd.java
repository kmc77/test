package net.comment.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.comment.db.CommentDAO;
import net.member.action.Action;
import net.member.action.ActionForward;

public class CommentAdd implements Action {

    @Override
    public ActionForward execute(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CommentDAO dao = new CommentDAO();

        Comment co = new Comment();
        co.setId(request.getParameter("id"));
        co.setContent(request.getParameter("content"));
        System.out.println("content=" + co.getContent());

        co.setComment_board_num(Integer.parseInt(request.getParameter("comment_board_num")));

        int ok = dao.commentsInsert(co);
        response.getWriter().print(ok);
        return null;
    }

}
