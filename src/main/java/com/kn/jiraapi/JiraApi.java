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

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class JiraApi{


    public static void main(String[] args) {
        SpringApplication.run(JiraApi.class, args);
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get("https://knits-hypersense-assets.atlassian.net/rest/api/3/issue/AMM-132")
                    .basicAuth(System.getenv("email"), System.getenv("jira_token"))
                    .header("Accept", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

        System.out.println(response.getBody());
        JiraClientService service = new JiraClientService();

        final String issueKey = service.createIssue("ABCD", 1L, "Issue created from JRJC");
        service.updateIssueDescription(issueKey, "This is description from my Jira Client");
        Issue issue = service.getIssue(issueKey);
        System.out.println(issue.getDescription());

        service.voteForAnIssue(issue);

        System.out.println(service.getTotalVotesCount(issueKey));

        service.addComment(issue, "This is comment from my Jira Client");

        List<Comment> comments = service.getAllComments(issueKey);
        comments.forEach(c -> System.out.println(c.getBody()));

        service.deleteIssue(issueKey, true);

    }
}