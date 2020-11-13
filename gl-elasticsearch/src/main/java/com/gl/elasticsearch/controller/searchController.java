package com.gl.elasticsearch.controller;

import com.gl.elasticsearch.service.SearchService;
import com.gl.elasticsearch.vo.SearchParams;
import com.gl.elasticsearch.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

@Controller
public class searchController {
    @Autowired
    SearchService searchService;

    @GetMapping("/list.html")
    public String toList(SearchParams searchParams, Model model, HttpServletRequest request) {
        searchParams.setQueryString(request.getQueryString());
        SearchResult searchResult = searchService.search(searchParams);
        model.addAttribute("result",searchResult);
        return "list";
    }
}
