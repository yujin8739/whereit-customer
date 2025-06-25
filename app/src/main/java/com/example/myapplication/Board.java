package com.example.myapplication;

import static com.example.myapplication.Database.address;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Board {


    private String documentId;//게시글 올린 사람
    private String title;//게시글 제목
    private String contents;//게시글 내용
    public static String p_name;//게시글 작성자 닉네임
    private String p_photo;//게시글 작성자 사진
    private String board_photo; //게시글에 등록할 사진
    private String like; //게시글 좋아요 개수
    @ServerTimestamp
    private Date date;
    private String phone;
    private String board_num;
    private String board_id;
    private String writer_id;
    private String address;
    private String price_up;
    private String longitude;
    private String latitude;

    public Board() {//빈생성자 생성

    }



    public Board(String documentId, String title, String contents, String address, String p_name, String p_photo, String board_num, String board_photo, String board_id, String writer_id,String like, String Latitude,String Longitude, String price_up, String phone) {//String p_name 잠시 보류

        this.documentId = documentId;
        this.title = title;
        this.phone = phone;
        this.contents = contents;
        this.address = address;
        this.p_name=p_name;
        this.p_photo=p_photo;
        this.board_num=board_num;
        this.board_photo=board_photo;
        this.like = like;
        this.board_id=board_id;
        this.writer_id=writer_id;
        this.longitude=Longitude;
        this.latitude=Latitude;
        this.price_up=price_up;

    }

    public String getBoard_id() {
        return board_id;

    }

    public void setBoard_id(String board_id) {
        this.board_id = board_id;
    }



    public String getBoard_num() {
        return board_num;
    }

    public void setBoard_num(String board_num) {
        this.board_num = board_num;
    }

    //alt+insert키르 누르면 클래스에 필요한 메소드 자동생성 가능
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String DocumentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getprice_up() {
        return price_up;
    }

    public void setprice_up(String price_up) {
        this.price_up = price_up;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }



    public String getP_photo() {
        return p_photo;
    }

    public void setP_photo(String p_photo) {
        this.p_photo = p_photo;
    }

    public String getBoard_photo() {
        return board_photo;
    }

    public void setBoard_photo(String board_photo) {
        this.board_photo = board_photo;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getWriter_id() {
        return writer_id;
    }
    public void setWriter_id(String writer_id) {
        this.writer_id = writer_id;
    }

    @Override
    public String toString() {
        return "Board{" +
                "documentId='" + documentId + '\'' +
                ", title='" + title + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", contents='" + contents + '\'' +
                ", address='" + address + '\'' +
                ", p_name='" + p_name + '\'' +
                ", p_photo='" + p_photo + '\'' +
                ", board_photo='" + board_photo + '\'' +
                ", like='" + like + '\'' +
                ", date=" + date +
                ", board_num='" + board_num + '\'' +
                ", board_id='" + board_id + '\'' +
                ", writer_id='" + writer_id + '\'' +
                ", price_up='" + price_up + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

}

