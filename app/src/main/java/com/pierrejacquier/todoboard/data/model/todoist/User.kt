package com.pierrejacquier.todoboard.data.model.todoist
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.squareup.moshi.Json
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
@Entity(tableName = "users")
data class User(

        @PrimaryKey(autoGenerate = false)
        val id: Long, //7833481

        @Json(name = "start_page") val startPage: String?, //overdue, today
        @Json(name = "avatar_small") val avatarSmall: String?, //https://dcff1xvirvpfp.cloudfront.net/7f5ac2e06e935f631a6c8eeeddcca7e0_small.jpg
        @Json(name = "completed_today") val completedToday: Int?, //5
        @Json(name = "is_premium") val isPremium: Boolean?, //true
        @Json(name = "sort_order") val sortOrder: Int?, //0
        @Json(name = "full_name") val fullName: String?, //Pierre Jacquier
        @Json(name = "auto_reminder") val autoReminder: Int?, //30
        @Json(name = "avatar_s640") val avatarS640: String?, //https://dcff1xvirvpfp.cloudfront.net/7f5ac2e06e935f631a6c8eeeddcca7e0_s640.jpg
        @Json(name = "share_limit") val shareLimit: Int?, //26
        @Json(name = "magic_num_reached") val magicNumReached: Boolean?, //true
        @Json(name = "next_week") val nextWeek: Int?, //1
        @Json(name = "completed_count") val completedCount: Int?, //661
        @Json(name = "daily_goal") val dailyGoal: Int?, //5
        @Json(name = "theme") val theme: Int?, //0
        @Json(name = "avatar_medium") val avatarMedium: String?, //https://dcff1xvirvpfp.cloudfront.net/7f5ac2e06e935f631a6c8eeeddcca7e0_medium.jpg
        @Json(name = "email") val email: String?, //pierrejacquier39@gmail.com
        @Json(name = "karma_disabled") val karmaDisabled: Int?, //1
        @Json(name = "start_day") val startDay: Int?, //1
        @Json(name = "avatar_big") val avatarBig: String?, //https://dcff1xvirvpfp.cloudfront.net/7f5ac2e06e935f631a6c8eeeddcca7e0_big.jpg
        @Json(name = "date_format") val dateFormat: Int?, //1
        @Json(name = "inbox_project") val inboxProject: Int?, //170467678
        @Json(name = "time_format") val prefer12h: Int?, //1
        @Json(name = "image_id") val imageId: String?, //7f5ac2e06e935f631a6c8eeeddcca7e0
        @Json(name = "karma_trend") val karmaTrend: String?, //up
        @Json(name = "business_account_id") val businessAccountId: Long?, //null
        @Json(name = "mobile_number") val mobileNumber: String?, //null
        @Json(name = "mobile_host") val mobileHost: String?, //null
        @Json(name = "premium_until") val premiumUntil: String?, //Sun 06 May 2018 08:34:04 +0000
        @Json(name = "dateist_lang") val dateistLang: String?, //null
        @Json(name = "join_date") val joinDate: String?, //Wed 11 May 2016 07:07:07 +0000
        @Json(name = "karma") val karma: Double?, //9691.0
        @Json(name = "is_biz_admin") val isBizAdmin: Boolean?, //false
        @Json(name = "default_reminder") val defaultReminder: String?, //push
        @Json(name = "dateist_inline_disabled") val dateistInlineDisabled: Boolean?, //false
        @Json(name = "token") var token: String?, //763374623daca69fe05e54131670fa70e215f7c6

        @Embedded(prefix = "tzinfo")
        @Json(name = "tz_info") val tzInfo: TzInfo?
): PaperParcelable {
        companion object {
                @JvmField val CREATOR = PaperParcelUser.CREATOR
        }
}

data class TzInfo(
        @Json(name = "hours") val hours: Int?, //8
        @Json(name = "timezone") val timezone: String?, //Asia/Singapore
        @Json(name = "is_dst") val isDst: Int?, //0
        @Json(name = "minutes") val minutes: Int?, //0
        @Json(name = "gmt_string") val gmtString: String? //+08:00
)