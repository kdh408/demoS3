package com.example.demo.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller//컨트롤러로 인식
public class BoardController {

    @Autowired  //의존성 주입.
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;

    @GetMapping("/board/write")    //게시글 작성 폼을 보여주는 역할
    public String boardWriteForm() {
        return "boardwrite";
    }

    @PostMapping("/board/writepro") //게시글 작성을 처리하고 작성 완료 메시지와 함께 메시지 페이지로 이동
    public String boardWritePro(Board board, Model model, @RequestParam("file") MultipartFile file) throws Exception{
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String email = userDetails.getUsername();

        boardService.write(board, file,email);

        model.addAttribute("message", "글 작성 완료");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    @GetMapping("/board/list")  //게시글 목록을 보여주는 페이지로 이동
    public String boardList(Model model, @PageableDefault(page = 0, size = 10, sort =  "id", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Board> list = boardService.boardList(pageable);

        int nowPage = list.getPageable().getPageNumber();
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist";
    }

    @GetMapping("/board/view") // localhost:8080/board/view?id=1 특정 게시글의 내용을 보여주는 페이지로 이동
    public String boardView(Model model, @RequestParam("id") Integer id) {

        model.addAttribute("board", boardService.boardView(id));

        return "boardview";
    }

    @GetMapping("/board/delete")    //특정 게시글을 삭제하고, 게시글 목록 페이지로 리다이렉트
    public String boardDelete(@RequestParam("id") Integer id, Board board) {
        //로그인 계정 받아오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal == null || !(principal instanceof UserDetails)) {
            return "redirect:/user/denied";
        }

        UserDetails userDetails = (UserDetails) principal;
        String login_email = userDetails.getUsername().toString();

        //게시글 작성자 계정 받아오기
        Board boardTemp = boardService.boardView(id);
        String write_email = boardTemp.getUser();

        Integer result= boardService.boardDelete(id,write_email,login_email);
        if(result==1){
            return "redirect:/board/list";
        }else return "redirect:/user/denied";

    }

    @GetMapping("/board/modify/{id}")   //특정 게시글을 수정하기 위한 수정 폼
    public String boardModify(@PathVariable("id") Integer id, Model model) {

        model.addAttribute("board", boardService.boardView(id));

        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")  //특정 게시글을 수정하고, 수정 완료 메시지와 함께 메시지 페이지로 이동
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, @RequestParam("file") MultipartFile file) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal == null || !(principal instanceof UserDetails)) {
            return "redirect:/user/denied";
        }

        UserDetails userDetails = (UserDetails) principal;
        String login_email = userDetails.getUsername().toString();

        System.out.println("첨부파일: "+board.getFilepath());
        Board boardTemp = boardService.boardView(id);
        boardTemp.setId(board.getId());
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());
        boardTemp.setFilepath(board.getFilepath());


        Integer result=boardService.modify(boardTemp, file,login_email);
        model.addAttribute("message", "글 수정 완료");
        model.addAttribute("searchUrl", "/board/list");
        
        if(result==1){
            return "message";    
        } else {
            return "redirect:/user/denied";
        }

    }

    //파일 다운로드
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer id) throws MalformedURLException {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        String filePath = System.getProperty("user.dir")+"/src/main/resources/static"+board.getFilepath();
        //String filePath = "/app/src/main/resources/static" + board.getFilepath();
        Path path = Paths.get(filePath);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("파일을 찾을 수 없습니다.");
        }

        String originalFileName = resource.getFilename();
        String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }
}