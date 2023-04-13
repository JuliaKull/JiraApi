package com.kn.jiraapi;


import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.kn.jiraapi.service.JiraClientService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class JiraApi {
    private static String issueKey = "AMM-132";

    public static void main(String[] args) {
        SpringApplication.run(JiraApi.class, args);

        JiraClientService service = new JiraClientService();
        //final String newIssueKey = service.createIssue("AMM", 2L, "Issue created from Java");

        service.updateIssueDescription(issueKey, "This is new description from Jira Client");
        Issue issue = service.getIssue(issueKey);
        System.out.println(issue.getDescription());

        service.voteForAnIssue(issue);

        System.out.println(service.getTotalVotesCount(issueKey));

        service.addComment(issue, "This is new comment from my Jira Client");

        List<Comment> comments = service.getAllComments(issueKey);
        comments.forEach(c -> System.out.println(c.getBody()));

        //service.deleteIssue(issueKey, true);

    }
}