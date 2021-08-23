package com.ubaworld.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ubaworld.R;
import com.ubaworld.activity.thread.MyThreadActivity;
import com.ubaworld.activity.thread.ThreadActivity;
import com.ubaworld.activity.askUba.AskUbaBuyingActivity;
import com.ubaworld.activity.askUba.AskUbaRentingActivity;
import com.ubaworld.activity.askUba.AskUbaUtilityActivity;
import com.ubaworld.model.CommentData;
import com.ubaworld.network.ApiService;
import com.ubaworld.network.RetroFitWebService;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.LogUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ubaworld.utils.Utils.convertDate_Time_InMilliseconds;
import static com.ubaworld.utils.Utils.getFromUserDefaults;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.loadImage;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.showViews;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyView> {

    private Activity activity;
    private CommentData commentData;
    private String authToken;
    private int user_id;
    private int type;

    public CommentAdapter(Activity activity, CommentData data, int type) {
        this.activity = activity;
        this.commentData = data;
        this.type = type;
    }

    class MyView extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_Verified)
        ImageView iv_Verified;

        @BindView(R.id.iv_profile_pic)
        CircleImageView iv_profile_pic;

        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        @BindView(R.id.tv_Comment)
        TextView tv_Comment;

        @BindView(R.id.ll_Like)
        LinearLayout ll_Like;

        @BindView(R.id.iv_Like)
        ImageView iv_Like;

        @BindView(R.id.tv_Like)
        TextView tv_Like;

        MyView(@NonNull final View view) {
            super(view);
            ButterKnife.bind(this, view);
            authToken = getFromUserDefaults(activity, Constants.AUTH_TOKEN);
            user_id = Integer.parseInt(getFromUserDefaults(activity, Constants.USER_ID));
        }

    }

    @NonNull
    @Override
    public CommentAdapter.MyView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_comment, null);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.MyView holder, int position) {
        CommentData.DataItem data = commentData.data.dataItems.get(position);

        if (data.is_verify == 1)
            showViews(holder.iv_Verified);
        else
            hideViews(holder.iv_Verified);

        if (data.is_liked == 1)
            holder.iv_Like.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.red_like_icon));
        else
            holder.iv_Like.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.black_dislike_icon));

        String imageId = data.profile_image;
        loadImage(activity, imageId, holder.iv_profile_pic, R.drawable.ic_profile_penguin, holder.progressBar);

        holder.tv_Comment.setText(data.comment);
        holder.tv_Like.setText(String.valueOf(data.likes));

        holder.ll_Like.setOnClickListener(view -> {
            if (data.is_liked == 0) {
                holder.iv_Like.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.red_like_icon));
                int like = data.likes + 1;
                holder.tv_Like.setText(String.valueOf(like));
            } else {
                holder.iv_Like.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.black_dislike_icon));
                int like = data.likes - 1;
                holder.tv_Like.setText(String.valueOf(like));
            }

            attempt_Like(data.id);
        });

        holder.tv_Comment.setOnClickListener(view -> {
            if (user_id == data.user_id) {
                Intent intent = new Intent(activity, MyThreadActivity.class);
                intent.putExtra("is_verify", data.is_verify);
                intent.putExtra("comment_id", data.id);
                intent.putExtra("user_id", data.user_id);
                intent.putExtra("profile_image", data.profile_image);
                intent.putExtra("comment", data.comment);
                intent.putExtra("like", data.likes);
                intent.putExtra("create_date", convertDate_Time_InMilliseconds(data.created_at));
                activity.startActivityForResult(intent, Constants.INTENT_COMMENT);
            } else {
                Intent intent = new Intent(activity, ThreadActivity.class);
                intent.putExtra("is_verify", data.is_verify);
                intent.putExtra("comment_id", data.id);
                intent.putExtra("user_id", data.user_id);
                intent.putExtra("profile_image", data.profile_image);
                intent.putExtra("comment", data.comment);
                intent.putExtra("like", data.likes);
//            intent.putExtra("replies", new Gson().toJson(data.replies));
                activity.startActivityForResult(intent, Constants.INTENT_COMMENT);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentData.data.dataItems.size();
    }

    private void attempt_Like(int id) {
        if (!isConnectingToInternet(activity))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.like_Comment(id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "LIKE CALLING" + logLine());
                        if (type == 1)
                            ((AskUbaRentingActivity) activity).onMethodCallback();
                        else if (type == 2)
                            ((AskUbaBuyingActivity) activity).onMethodCallback();
                        else
                            ((AskUbaUtilityActivity) activity).onMethodCallback();

                    } else {
                        try {
                            LogUtils.e("ERROR", " ---> " + response.raw().message() + logLine());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                LogUtils.e("FAILED", " ---> " + t.getMessage() + logLine());
            }
        });
    }

}
