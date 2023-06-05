package com.example.solo_project;

import org.webrtc.SessionDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    //로그인 기능

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
    Call<ArrayList<item_model>> select_used(@Query("page") int page);

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
    Call<Signup_model> used_insert(@Part("nickname") String nickname, @Part("used_name") String used_name, @Part("detail") String detail, @Part("price") int price, @Part List<MultipartBody.Part> multi, @Part("image_size") int image_size, @Part("date") String date);

    //판매중 아이템 불러오기
    @GET("select_sale_history.php")
    Call<ArrayList<item_model>> select_sale_history(@Query("nickname") String nickname);

    //채팅에서 파일 보내기
    @Multipart
    @POST("chat_file_upload.php")
    //채팅 이미지 업로드
    Call<String> chat_file_upload(@Part MultipartBody.Part file, @Part("file_num") String file_num);

    //token db에 저장하기
    @GET("FCM_token_update.php")
    // 토큰 업테이트
    Call<String> token_update(@Query("token") String token, @Query("nickname") String nickname);

    @GET("chat_room_check.php")
        //룸 체크
    Call<String> chat_room_check(@Query("user1") String user1, @Query("user2") String user2);

    //약속 잡기
    @GET("chat_promise_insert.php")
    // 약속 잡기
    Call<String> chat_promise_insert(@Query("room_num") String room_num, @Query("promise_date") String date, @Query("promise_time") String time, @Query("nickname") String nickname);

    @GET("chat_promise_select.php")
        // 약속 셀렉트
    Call<chat_promise_model> chat_promise_select(@Query("room_num") String room_num);

    @GET("sold_out.php")
        // 판매완료
    Call<String> used_sold_out(@Query("num") String num, @Query("sold_out_status") String sold_out_status);

    @GET("sold_out_select.php")
        // 거래 완료한 아이템 불러오기
    Call<ArrayList<item_model>> used_sold_out_select(@Query("nickname") String nickname);

    @GET("used_delete.php")
    Call<String> used_delete(@Query("num") String num);

    @Headers("Authorization:KakaoAK 9b1ecb73c47f7aef033dce378f48e0fa")
    @FormUrlEncoded
    @POST("/v1/payment/ready")
    Call<Kakao_pay_item_model> kakao_pay_ready_request(
            @FieldMap Map<String ,String> map);
    @Headers("Authorization:KakaoAK 9b1ecb73c47f7aef033dce378f48e0fa")
    @FormUrlEncoded
    @POST("/v1/payment/approve")
    Call<Payment> kakao_pay_approve_request(
            @FieldMap Map<String,String> map
    );
    @GET("account_getCredit.php")
    Call<Signup_model> getCredit(@Query("nickname") String nickname);
    @GET("update_Credit.php")
    Call<Signup_model> update_Credit(@Query("nickname") String nickname,@Query("Credit") String Credit,@Query("pg_token") String pg_token);
    @Multipart
    @POST("used_update.php")
    Call<Signup_model> used_update(@Part("num") String num,@Part("usedname") String used_name, @Part("detail") String detail, @Part("price") int price, @Part List<MultipartBody.Part> add_file_list,@Part("del_file_list[]") List<String> del_file_list,@Part("image_size") int add_image_size);
    @Multipart
    @POST("test.php")
    Call<String> test_php(@Part("del_file_list[]") List<String> del_file_list);
    @FormUrlEncoded
    @POST("webrtc_offer.php")
    Call<String> send_offer(@Field("sdp") String sdp,@Field("receiver") String nickname);

    @GET("used_search.php")
        // 검색기능
    Call<ArrayList<item_model>> used_search(@Query("comment") String comment);
}
