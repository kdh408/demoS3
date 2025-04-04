package com.example.demo.board;

import com.example.demo.login.LoginRepository;
import com.example.demo.s3connection.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private LoginRepository loginRepository;
    //@Autowired
    //private S3Uploader s3Uploader;

    //글 작성
    public void write(Board board, MultipartFile file, String email) throws Exception {

        String projectPath = System.getProperty("user.dir")+"/src/main/resources/static/files";

        if (file.getOriginalFilename().length() > 0 ) {
            //String projectPath = "/app/src/main/resources/static/files";

            //UUID uuid = UUID.randomUUID();
            //String fileName = uuid+ "_"+file.getOriginalFilename();
            //String fileName = board.getId()+ "_" +file.getOriginalFilename();
            String fileName = file.getOriginalFilename();
            File saveFile = new File(projectPath, fileName);

            file.transferTo(saveFile);

            board.setFilename(file.getOriginalFilename());
            board.setFilepath("/files/" +fileName);

        }

        System.out.println("파일 제목: "+file.getOriginalFilename());
        board.setUser(email);
        String name = loginRepository.findByEmail(email).get().getName().toString();
        board.setWriter(name);

        //s3 Upload
        /*if (file.getOriginalFilename().length() > 0 ) {
            String imgURL = s3Uploader.upload(file);
            board.setFilepath(imgURL);
        }*/

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
        if (write_email.equals(login_email)) {
            boardRepository.deleteById(id);
            return 1;
        } else{
            return 0;
        }

    }

    public String sanitizePath(String filePath) {
        // 1. 경로에서 URL 인코딩된 ./, ../, \, %2E, %2F 등을 필터링
        // %2F (%2F는 '/'), %2E (%2E는 '.') 등의 패턴을 제거
        filePath = filePath.replaceAll("%2F", "/")  // %2F -> /
                .replaceAll("%2E", ".")  // %2E -> .
                .replaceAll("%5C", "")    // %5C -> \ 제거
                .replaceAll("\\.\\./", "") // ../ 등 디렉토리 탐색 제거
                .replaceAll("/\\./", "")   // ./ 등 상대 경로 제거
                .replaceAll("//", "/");    // 연속된 '/'를 하나로 변환

        // 경로에서 `..`이나 `.`과 같은 상대경로 요소가 있으면 제거
        filePath = filePath.replaceAll("\\.\\.", "")  // ".." 제거
                .replaceAll("\\./", "/");  // "./" 제거

        return filePath;
    }
}
