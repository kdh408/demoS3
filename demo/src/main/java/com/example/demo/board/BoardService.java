package com.example.demo.board;

import com.example.demo.login.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private LoginRepository loginRepository;

    //글 작성
    public void write(Board board, MultipartFile file, String email) throws Exception {

        String projectPath = System.getProperty("user.dir")+"/src/main/resources/static/files";

        if (file.getOriginalFilename().length() > 0 ) {
            //String projectPath = "/app/src/main/resources/static/files";

            String fileName = file.getOriginalFilename();
            File saveFile = new File(projectPath, fileName);

            file.transferTo(saveFile);

            board.setFilename(file.getOriginalFilename());
            board.setFilepath("/files/" +fileName);

        }

        board.setUser(email);
        String name = loginRepository.findByEmail(email).get().getName().toString();
        board.setWriter(name);

        boardRepository.save(board);

    }

    //글 수정
    public Integer modify(Board board, MultipartFile file, String login_email) throws Exception {
        String write_email = board.getUser();

        if (write_email.equals(login_email)) {

            String projectPath = System.getProperty("user.dir")+"/src/main/resources/static/files";
            if (file.getOriginalFilename().length() > 0 ) {
                //String projectPath = "/app/src/main/resources/static/files";

                String fileName = file.getOriginalFilename();
                File saveFile = new File(projectPath, fileName);

                file.transferTo(saveFile);

                board.setFilename(file.getOriginalFilename());
                board.setFilepath("/files/" +fileName);

            }else{
                board.setFilename(file.getOriginalFilename());
            }

            boardRepository.save(board);
            return 1;
        } else{
            return 0;
        }

    }


    //게시글 리스트 처리
    public Page<Board> boardList(Pageable pageable) {

        return boardRepository.findAll(pageable);
    }


    //특정한 게시글 불러오기
    public Board boardView(Integer id) {
        return boardRepository.findById(id).get();
    }


    public Integer boardDelete(Integer id, String write_email, String login_email) {
        if (write_email.equals(login_email) || write_email.equals("admin@example.com")) {
            boardRepository.deleteById(id);
            return 1;
        } else{
            return 0;
        }

    }

}
