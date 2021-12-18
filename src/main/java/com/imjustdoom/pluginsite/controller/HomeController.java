package com.imjustdoom.pluginsite.controller;

import com.imjustdoom.pluginsite.model.Account;
import com.imjustdoom.pluginsite.model.Blog;
import com.imjustdoom.pluginsite.repositories.AccountRepository;
import com.imjustdoom.pluginsite.repositories.BlogRepository;
import lombok.AllArgsConstructor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Controller
@AllArgsConstructor
public class HomeController {

    private final BlogRepository blogRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/")
    public String home(Model model, Account account) {
        for (Blog blog : blogRepository.findAll()) {
            int authorId = blog.getAuthorId();
            Account author = accountRepository.findById(authorId).get();
            blog.setAuthorName(author.getUsername());

            LocalDateTime date = blog.getCreatedDate();
            String dateString = date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear() + ", " + date.getHour() + ":" + date.getMinute();
            blog.setDateString(dateString);

            String md_body = blog.getPost();
            md_body = md_body.replaceAll("script", "error style=\"display:none;\"");
            Parser parser = Parser.builder().build();

            Node document = parser.parse(md_body);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            blog.setPost(renderer.render(document));
        }

        model.addAttribute("account", account);

        List<Blog> blogs = blogRepository.findAll();
        Collections.reverse(blogs);
        model.addAttribute("blogs", blogs);
        return "home";
    }
}