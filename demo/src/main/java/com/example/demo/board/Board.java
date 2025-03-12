package com.example.demo.board;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Entity
@Data
@Table(name = "board")

public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String content;

  //  private String filename;

    private String filepath;

    private String user;

    private String writer;

    public void setFilename(String fileName) {
    }
}
