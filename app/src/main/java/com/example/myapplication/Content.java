package com.example.myapplication;


import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Content {
    private String c_name;//댓글 단사람 닉네임
    private String c_photo; //댓글 단사람 사진
    private String comment;//댓글
    private String documentId;//댓글 단사람 고유식별번호
    private String Latitude;
    private String Longitude;
    private String price_up;
    private String boar_position;
    private String phone;
    private String address;
    private String boar_title;
    @ServerTimestamp
    private Date comment_date;
    private String comment_boar;



    public Content(String doucumentId, String c_name, String comment, String boar_position, String boar_title, String c_photo, String comment_boar,String latitude, String longitude, String price_up,String address, String phone) {
        this.c_name = c_name;
        this.c_photo = c_photo;
        this.comment = comment;
        this.documentId=doucumentId;
        this.boar_position=boar_position;
        this.boar_title=boar_title;
        this.comment_boar="comment_boar";
        this.Latitude=latitude;
        this.Longitude=longitude;
        this.price_up = price_up;
        this.address = address;
        this.phone = phone;
    }

    public String getPost_title() {
        return boar_title;
    }

    public void setPost_title(String boar_title) {
        this.boar_title = boar_title;
    }

    public String getPost_position() {
        return boar_position;
    }

    public void setPost_position(String boar_position) {
        this.boar_position = boar_position;
    }



    public Date getComment_date() {
        return comment_date;
    }

    public void setComment_date(Date comment_date) {
        this.comment_date = comment_date;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getC_photo() {
        return c_photo;
    }

    public void setC_photo(String c_photo) {
        this.c_photo = c_photo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_boar() {
        return comment_boar;
    }

    public void setComment_boar(String comment_boar) {
        this.comment_boar = comment_boar;
    }




    @Override
    public String toString() {
        return "Content{" +
                "c_name='" + c_name + '\'' +
                ", c_photo='" + c_photo + '\'' +
                ", comment='" + comment + '\'' +
                ", documentId='" + documentId + '\'' +
                ", boar_position='" + boar_position + '\'' +
                ", boar_title='" + boar_title + '\'' +
                ", comment_date=" + comment_date +
                ", comment_boar='" + comment_boar + '\'' +
                ", price_up='" + price_up + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
