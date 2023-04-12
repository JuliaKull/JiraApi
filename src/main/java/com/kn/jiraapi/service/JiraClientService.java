package com.kn.jiraapi.service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class JiraClientService {

    private static String url = "https://knits-hypersense-assets.atlassian.net/rest/api/3/issue/"


    public String createIssue(String projectKey, Long issueType, String issueSummary) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post(url)
                    .basicAuth(System.getenv("email"), System.getenv("jira_token"))
                    .header("Content-Type", "application/json")
                    .body("{\"fields\": {\"project\":{\"key\":\"" + projectKey + "\"}," +
                            "\"summary\":\"" + issueSummary + "\"," +
                            "\"issuetype\":{\"id\":\"" + issueType + "\"}}}")
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
        return response.getBody().getObject().getString("key");
    }

    public void updateIssueDescription(String issueKey, String newDescription) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.put(url+issueKey)
                    .basicAuth(System.getenv("email"), System.getenv("jira_token"))
                    .header("Content-Type", "application/json")
                    .body("{\"update\":{\"description\":[{\"set\":\"" + newDescription + "\"}]}}")
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public Issue getIssue(String issueKey) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(url+issueKey)
                    .basicAuth(System.getenv("email"), System.getenv("jira_token"))
                    .header("Accept", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
        JsonObject issueObject = response.getBody().getObject();
        return new Gson().fromJson(issueObject.toString(), Issue.class);
    }

    public void voteForAnIssue(Issue issue) {
        String voteUrl = String.valueOf(issue.getVotes().getSelf());
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post(voteUrl)
                    .basicAuth(System.getenv("email"), System.getenv("jira_token"))
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTotalVotesCount(String issueKey) {
        Issue issue = getIssue(issueKey);
        String voteUrl = String.valueOf(issue.getVotes().getSelf());
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(voteUrl)
                    .basicAuth(System.getenv("email"), System.getenv("jira_token"))
                    .header("Accept", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
        JsonObject votesObject = response.getBody().getObject();
        return votesObject.getInt("votes");
    }

    public void addComment(Issue issue, String commentBody) {
        String commentUrl = issue.getComments().getSelf();
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post(commentUrl)
                    .basicAuth(System.getenv("email"), System.getenv("jira_token"))
                    .header("Content-Type", "application/json")
                    .body("{\"body\":\"" + commentBody + "\"}")
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Comment> getAllComments(String issueKey) {
        String commentsUrl = url + issueKey + "/comment";
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.get(commentsUrl)
                    .basicAuth(System.getenv("email"), System.getenv("jira_token"))
                    .header("Accept", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

        JSONArray commentsArray = response.getBody().getObject().getJSONArray("comments");
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < commentsArray.length(); i++) {
            JSONObject commentObject = commentsArray.getJSONObject(i);
            Comment comment = new Comment(commentObject.getString("id"), commentObject.getString("body"));
            comments.add(comment);
        }
        return comments;
    }

    public void deleteIssue(String issueKey, boolean deleteSubtasks) {
        String deleteUrl = url + issueKey + "?deleteSubtasks=" + deleteSubtasks;
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.delete(deleteUrl)
                    .basicAuth(System.getenv("email"), System.getenv("jira_token"))
                    .header("Accept", "application/json")
                    .asJson();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }





}
