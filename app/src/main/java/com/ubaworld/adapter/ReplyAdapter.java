package com.ubaworld.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ubaworld.R;
import com.ubaworld.activity.thread.MyThreadActivity;
import com.ubaworld.activity.thread.ThreadActivity;
import com.ubaworld.activity.thread.UpdateCommentReplyActivity;
import com.ubaworld.model.ReplyData;
import com.ubaworld.network.ApiService;
import com.ubaworld.network.RetroFitWebService;
import com.ubaworld.utils.Constants;
import com.ubaworld.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static com.ubaworld.utils.Utils.convertDate_Time_InMilliseconds;
import static com.ubaworld.utils.Utils.getCurrentDate_Thread;
import static com.ubaworld.utils.Utils.getFromUserDefaults;
import static com.ubaworld.utils.Utils.hideViews;
import static com.ubaworld.utils.Utils.isConnectingToInternet;
import static com.ubaworld.utils.Utils.loadImage;
import static com.ubaworld.utils.Utils.logLine;
import static com.ubaworld.utils.Utils.showValidationAlertDialog;
import static com.ubaworld.utils.Utils.showViews;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.MyView> {

    private Activity activity;
    private ReplyData replyData;
    private LinearLayout llView;
    private int type;
    private String authToken;
    private int user_id;

    public ReplyAdapter(Activity activity, ReplyData replyData, LinearLayout llView, int type) {
        this.activity = activity;
        this.replyData = replyData;
        this.llView = llView;
        this.type = type;
    }

    class MyView extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_profile_pic)
        CircleImageView iv_profile_pic;

        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        @BindView(R.id.iv_Verified)
        ImageView iv_Verified;

        @BindView(R.id.tv_Reply)
        TextView tv_Reply;

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
    public ReplyAdapter.MyView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.list_item_reply, null);
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReplyAdapter.MyView holder, int position) {
        ReplyData.Replies data = replyData.data.get(0).replies.get(position);

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

        holder.tv_Reply.setText(data.reply);
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

        holder.tv_Reply.setOnClickListener(view -> {
            if (user_id == data.user_id) {
                showMyThreadPopup(data);
            } else {
                showThreadPopup(data);
            }
        });

    }

    @Override
    public int getItemCount() {
        return replyData.data.get(0).replies.size();
    }

    private void showMyThreadPopup(ReplyData.Replies replies) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popup_my_thread_report, null);

        TextView tv_Edit = view.findViewById(R.id.tv_Edit);
        TextView tv_Delete = view.findViewById(R.id.tv_Delete);
        TextView tv_Cancel = view.findViewById(R.id.tv_Cancel);
        LinearLayout ll_DeleteView = view.findViewById(R.id.ll_DeleteView);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(llView, Gravity.BOTTOM, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);

        long diff = (convertDate_Time_InMilliseconds(getCurrentDate_Thread()) - convertDate_Time_InMilliseconds(replies.created_at)) / 1000;
        LogUtils.e("TIME", " :: " + diff + " sec" + logLine());
        if (diff > 60)
            hideViews(ll_DeleteView);

        tv_Edit.setOnClickListener(view12 -> {
            popupWindow.dismiss();
            Intent intent = new Intent(activity, UpdateCommentReplyActivity.class);
            intent.putExtra("type", "reply");
            intent.putExtra("is_verify", replies.is_verify);
            intent.putExtra("id", replies.id);
            intent.putExtra("profile_image", replies.profile_image);
            intent.putExtra("text", replies.reply);
            activity.startActivityForResult(intent, Constants.INTENT_THREAD_REPLY);
        });

        tv_Delete.setOnClickListener(view1 -> {
            popupWindow.dismiss();
//            long diff = (convertDate_Time_InMilliseconds(getCurrentDate_Thread()) - convertDate_Time_InMilliseconds(replies.created_at)) / 1000;
//            LogUtils.e("TIME", " :: " + diff + " sec" + logLine());
//            if (diff < 61)
            deleteReply(replies.id);
//            else
//                showValidationAlertDialog(activity, "You don't have permission to delete this question.");
        });

        tv_Cancel.setOnClickListener(v -> popupWindow.dismiss());
    }

    private void showThreadPopup(ReplyData.Replies replies) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.popup_thread_report, null);

        TextView tv_Report = view.findViewById(R.id.tv_Report);
        TextView tv_Cancel = view.findViewById(R.id.tv_Cancel);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(llView, Gravity.BOTTOM, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);

        tv_Report.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            reportUser(replies.user_id);
        });

        tv_Cancel.setOnClickListener(v -> popupWindow.dismiss());
    }

    private void attempt_Like(int id) {
        if (!isConnectingToInternet(activity))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.like_Reply(id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "LIKE CALLING" + logLine());
                        if (type == 1)
                            ((ThreadActivity) activity).onMethodCallback();
                        else
                            ((MyThreadActivity) activity).onMethodCallback();

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

    private void deleteReply(int id) {
        if (!isConnectingToInternet(activity))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.delete_Reply(id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "REPLY DELETE" + logLine());
                        if (type == 1)
                            ((ThreadActivity) activity).onMethodCallback();
                        else
                            ((MyThreadActivity) activity).onMethodCallback();
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

    private void reportUser(int userId) {
        if (!isConnectingToInternet(activity))
            return;

        ApiService apiService = RetroFitWebService.generateServiceWithToken(ApiService.class, authToken);
        Call<ResponseBody> call = apiService.report_User(userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        LogUtils.e("RESPONSE", "REPORT USER" + logLine());

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
