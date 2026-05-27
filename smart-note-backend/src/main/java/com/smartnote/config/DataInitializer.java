package com.smartnote.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartnote.entity.*;
import com.smartnote.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserFollowMapper userFollowMapper;
    @Autowired
    private UserLikeMapper userLikeMapper;
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userMapper.selectCount(new LambdaQueryWrapper<>()) > 0) {
            return;
        }

        String pwd = passwordEncoder.encode("123456");

        // ===== 用户 =====
        User u1 = new User();
        u1.setId(1L); u1.setUsername("小明"); u1.setEmail("test1@qq.com"); u1.setPassword(pwd);
        u1.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=xiaoming");
        u1.setBio("热爱生活的湖科人~"); u1.setGender(1); u1.setBirthday(LocalDate.of(2000, 5, 15));
        u1.setFollowerCount(2); u1.setFollowingCount(2); u1.setNoteCount(3);
        userMapper.insert(u1);

        User u2 = new User();
        u2.setId(2L); u2.setUsername("小红"); u2.setEmail("test2@qq.com"); u2.setPassword(pwd);
        u2.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=xiaohong");
        u2.setBio("记录校园美好时光"); u2.setGender(2); u2.setBirthday(LocalDate.of(2001, 3, 20));
        u2.setFollowerCount(1); u2.setFollowingCount(1); u2.setNoteCount(2);
        userMapper.insert(u2);

        User u3 = new User();
        u3.setId(3L); u3.setUsername("小刚"); u3.setEmail("test3@qq.com"); u3.setPassword(pwd);
        u3.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=xiaogang");
        u3.setBio("代码改变世界"); u3.setGender(1); u3.setBirthday(LocalDate.of(1999, 11, 8));
        u3.setFollowerCount(1); u3.setFollowingCount(1); u3.setNoteCount(1);
        userMapper.insert(u3);

        // ===== 关注关系 =====
        insertFollow(1L, 2L); // 小明 -> 小红
        insertFollow(1L, 3L); // 小明 -> 小刚
        insertFollow(2L, 1L); // 小红 -> 小明
        insertFollow(3L, 1L); // 小刚 -> 小明

        // ===== 笔记 =====
        Note n1 = buildNote(1L, 1L, "湖科图书馆日落太美了！",
                "今天在图书馆自习，傍晚时分窗外的日落真的绝了，整个天空都是金色的，强烈推荐这个位置！就在三楼靠窗区。",
                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=400", "校园风景", 128, 2, 2, 1);
        Note n2 = buildNote(2L, 2L, "食堂二楼的麻辣香锅测评",
                "新开的麻辣香锅窗口真的太可了！人均15块，量超级大，强烈推荐加午餐肉和土豆片。不要加太多辣，微辣就够了~",
                "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400", "美食", 88, 1, 1, 1);
        Note n3 = buildNote(3L, 1L, "计算机学院考研经验分享",
                "复习408的同学们注意了，数据结构和计组是重点，操作系统和计网要多做题。整理了近5年真题，需要的同学私我。",
                "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400", "学习", 256, 1, 2, 2);
        Note n4 = buildNote(4L, 2L, "周末打卡了学校附近的樱花林",
                "就在北门外走10分钟，现在正是樱花盛开的季节，拍照超出片！建议早上8点去，人少光线好。",
                "https://images.unsplash.com/photo-1522383225653-ed111181a951?w=400", "校园风景", 66, 1, 1, 0);
        Note n5 = buildNote(5L, 3L, "Spring Boot + Vue3 全栈笔记项目",
                "用 Spring Boot + Vue 3 做了一个笔记应用，支持登录注册、发布笔记、评论点赞收藏功能，前后端分离，代码已上传 GitHub。",
                "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400", "学习", 189, 1, 1, 1);
        Note n6 = buildNote(6L, 1L, "推荐一个超好用的背单词App",
                "墨墨背单词强烈安利！根据记忆曲线自动安排复习，比死记硬背高效太多了。坚持了一个月，词汇量涨了1000+",
                "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400", "读书笔记", 42, 1, 1, 0);

        // ===== 评论 =====
        insertComment(1L, 1L, 2L, "我也经常去三楼！那个位置确实拍照绝佳");
        insertComment(2L, 1L, 3L, "求具体位置，我明天就去");
        insertComment(3L, 2L, 1L, "今天就冲！人均15真的假的");
        insertComment(4L, 3L, 2L, "学长可以分享下真题吗？感谢！");
        insertComment(5L, 5L, 1L, "大佬带带！求GitHub链接");
        insertComment(6L, 5L, 2L, "这个项目用到了哪些技术栈？");
        insertComment(7L, 3L, 3L, "已收藏，考研党表示感谢！");
        insertComment(8L, 1L, 1L, "欢迎大家来三楼打卡~");

        // ===== 点赞 =====
        insertLike(1L, 2L); insertLike(1L, 4L);
        insertLike(2L, 1L); insertLike(2L, 3L);
        insertLike(3L, 1L); insertLike(3L, 5L);

        // ===== 收藏 =====
        insertFavorite(1L, 3L);
        insertFavorite(2L, 1L); insertFavorite(2L, 5L);
        insertFavorite(3L, 3L); insertFavorite(3L, 5L);

        System.out.println("=== 测试数据初始化完成 ===");
        System.out.println("=== 3个用户 | 6条笔记 | 8条评论 | 4条关注 | 6个点赞 | 5个收藏 ===");
        System.out.println("=== 登录: test1@qq.com / test2@qq.com / test3@qq.com  密码: 123456 ===");
    }

    private void insertFollow(Long followerId, Long followingId) {
        UserFollow f = new UserFollow();
        f.setFollowerId(followerId);
        f.setFollowingId(followingId);
        userFollowMapper.insert(f);
    }

    private Note buildNote(Long id, Long userId, String title, String content, String cover, String category,
                           int views, int likes, int comments, int favs) {
        Note n = new Note();
        n.setId(id); n.setUserId(userId); n.setTitle(title); n.setContent(content);
        n.setCoverImage(cover); n.setCategory(category);
        n.setViewCount(views); n.setLikeCount(likes); n.setCommentCount(comments); n.setFavoriteCount(favs);
        n.setCreatedAt(LocalDateTime.now().minusHours(id * 3));
        n.setUpdatedAt(LocalDateTime.now().minusHours(id * 3));
        noteMapper.insert(n);
        return n;
    }

    private void insertComment(Long id, Long noteId, Long userId, String content) {
        Comment c = new Comment();
        c.setId(id); c.setNoteId(noteId); c.setUserId(userId); c.setContent(content);
        c.setCreatedAt(LocalDateTime.now().minusHours(id * 2));
        commentMapper.insert(c);
    }

    private void insertLike(Long userId, Long noteId) {
        UserLike l = new UserLike();
        l.setUserId(userId); l.setNoteId(noteId);
        userLikeMapper.insert(l);
    }

    private void insertFavorite(Long userId, Long noteId) {
        UserFavorite f = new UserFavorite();
        f.setUserId(userId); f.setNoteId(noteId);
        userFavoriteMapper.insert(f);
    }
}
