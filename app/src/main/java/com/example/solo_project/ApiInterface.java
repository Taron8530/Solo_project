package com.example.solo_project;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    //로그인 기능
    @GET("test.php")
    Call<String> test(
            @Query("email") String email
    );
    @GET("account_select.php")
    Call<Signup_model> getLogin(
            @Query("email") String email,
            @Query("PW") String PW
    );
    //회원가입
    @GET("account_insert.php")
    Call<Signup_model> insertaccount(
            @Query("email") String email,
            @Query("PW") String PW,
            @Query("nickname") String nickname
    );
    //닉네임 체크(회원가입 절차)
    @GET("check_nickname.php")
    Call<String> checknickname(
            @Query("nickname") String nickname
    );
    //이메일 체크(회원가입 절차)
    @GET("check_email.php")
    Call<String> check_email(
            @Query("email") String email
    );
    //메인 리사이클러뷰로 뿌려주는 용도
    @GET("select_used.php")
    Call <ArrayList<item_model>> select_used();
    //인증코드로 프로필 불러오기
    @GET("select_profile.php")
    Call<Signup_model> profile_sel(@Query("verify") String verify);
    //프로필 이미지 업로드
    @Multipart
    @POST("upload_image.php")
    Call<Signup_model> uploadImage(@Part MultipartBody.Part File);
    //물건 올리기
    @Multipart
    @POST("used_insert.php")
    Call<Signup_model> used_insert(@Part("nickname") String nickname, @Part("used_name") String used_name, @Part("detail") String detail, @Part("price") int price, @Part List<MultipartBody.Part> multi, @Part("image_size") int image_size, @Part("date")String date);
    //판매중 아이템 불러오기
    @GET("select_sale_history.php")
    Call<ArrayList<item_model>> select_sale_history(@Query("nickname") String nickname);
    //채팅에서 파일 보내기
    @Multipart
    @POST("chat_file_upload.php")
    Call<String> chat_file_upload(@Part MultipartBody.Part file,@Part("file_num") String file_num);
    //token db에 저장하기
    @GET("FCM_token_update.php")
    Call<String> token_update(@Query("token") String token,@Query("nickname") String nickname);
}
