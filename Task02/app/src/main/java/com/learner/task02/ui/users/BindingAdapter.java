package com.learner.task02.ui.users;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.learner.task02.R;
import com.learner.task02.data.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class BindingAdapter {
    private static String WOMEN_PROFILE_PIC_BASE_URL = "https://randomuser.me/api/portraits/women/";
    private static String MEN_PROFILE_PIC_BASE_URL = "https://randomuser.me/api/portraits/men/";

    @androidx.databinding.BindingAdapter(value = "setImageUrl")
    public static void setProfileImage(CircleImageView imageView, User user) {
        if (user.getGender().equalsIgnoreCase("male")) {
            String imageUrl = MEN_PROFILE_PIC_BASE_URL + user.getPhoto() + ".jpg";
            Glide.with(imageView).load(imageUrl).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .into(imageView);
        } else if (user.getGender().equalsIgnoreCase("female")) {
            String imageUrl = WOMEN_PROFILE_PIC_BASE_URL + user.getPhoto() + ".jpg";
            Glide.with(imageView).load(imageUrl).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .into(imageView);
        } else {
            Glide.with(imageView).load(R.drawable.user_placeholder).apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder)
                    .into(imageView);
        }
    }
}
