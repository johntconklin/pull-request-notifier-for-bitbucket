package se.bjurr.prnfs.admin.utils;

import static com.atlassian.stash.pull.PullRequestAction.COMMENTED;
import static com.atlassian.stash.pull.PullRequestAction.RESCOPED;
import static com.atlassian.stash.pull.PullRequestRole.PARTICIPANT;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.bjurr.prnfs.admin.utils.PullRequestRefBuilder.pullRequestRefBuilder;

import java.util.Set;

import com.atlassian.stash.comment.Comment;
import com.atlassian.stash.event.pull.PullRequestCommentAddedEvent;
import com.atlassian.stash.event.pull.PullRequestEvent;
import com.atlassian.stash.event.pull.PullRequestRescopedEvent;
import com.atlassian.stash.pull.PullRequest;
import com.atlassian.stash.pull.PullRequestAction;
import com.atlassian.stash.pull.PullRequestParticipant;
import com.atlassian.stash.pull.PullRequestRole;
import com.atlassian.stash.pull.PullRequestState;

public class PullRequestEventBuilder {
 public static final String PREVIOUS_TO_HASH = "previousToHash";
 public static final String PREVIOUS_FROM_HASH = "previousFromHash";
 private PullRequestAction pullRequestAction;
 private PullRequestRefBuilder toRef = pullRequestRefBuilder();
 private PullRequestRefBuilder fromRef = pullRequestRefBuilder();
 private PullRequestParticipant author;
 private String commentText;
 private final PrnfsTestBuilder prnfsTestBuilder;
 private boolean beingClosed;
 private final boolean beingOpen = TRUE;
 private Long pullRequestId = 0L;
 private final Set<PullRequestParticipant> participants = newHashSet();
 private final Set<PullRequestParticipant> reviewers = newHashSet();
 private PullRequestState pullRequestState;

 private PullRequestEventBuilder(PrnfsTestBuilder prnfsTestBuilder) {
  this.prnfsTestBuilder = prnfsTestBuilder;
 }

 public PullRequestEventBuilder withParticipant(PullRequestRole role, Boolean isApproved) {
  PullRequestParticipant participant = mock(PullRequestParticipant.class);
  when(participant.isApproved()).thenReturn(isApproved);
  if (role == PARTICIPANT) {
   participants.add(participant);
  } else {
   reviewers.add(participant);
  }
  return this;
 }

 public PullRequestEventBuilder withFromRef(PullRequestRefBuilder fromRef) {
  this.fromRef = fromRef;
  return this;
 }

 public PullRequestEventBuilder withToRef(PullRequestRefBuilder toRef) {
  this.toRef = toRef;
  return this;
 }

 public static PullRequestEventBuilder pullRequestEventBuilder() {
  return new PullRequestEventBuilder(null);
 }

 public static PullRequestEventBuilder pullRequestEventBuilder(PrnfsTestBuilder prnfsTestBuilder) {
  return new PullRequestEventBuilder(prnfsTestBuilder);
 }

 public PrnfsTestBuilder getPrnfsTestBuilder() {
  return prnfsTestBuilder;
 }

 public PullRequestRefBuilder withFromRefPullRequestRefBuilder() {
  PullRequestRefBuilder ref = pullRequestRefBuilder(this);
  this.withFromRef(ref);
  return ref;
 }

 public PullRequestRefBuilder withToRefPullRequestRefBuilder() {
  PullRequestRefBuilder ref = pullRequestRefBuilder(this);
  this.withToRef(ref);
  return ref;
 }

 public PullRequestEventBuilder withPullRequestAction(PullRequestAction pullRequestAction) {
  this.pullRequestAction = pullRequestAction;
  return this;
 }

 public PullRequestEventBuilder withAuthor(PullRequestParticipant author) {
  this.author = author;
  return this;
 }

 public PullRequestEventBuilder withCommentText(String commentText) {
  this.commentText = commentText;
  return this;
 }

 public PullRequestEvent build() {
  PullRequestEvent pullRequestEvent = mock(PullRequestEvent.class);
  if (pullRequestAction == RESCOPED) {
   PullRequestRescopedEvent event = mock(PullRequestRescopedEvent.class);
   when(event.getPreviousFromHash()).thenReturn(PREVIOUS_FROM_HASH);
   when(event.getPreviousToHash()).thenReturn(PREVIOUS_TO_HASH);
   pullRequestEvent = event;
  } else if (pullRequestAction == COMMENTED) {
   PullRequestCommentAddedEvent event = mock(PullRequestCommentAddedEvent.class);
   Comment comment = mock(Comment.class);
   when(event.getComment()).thenReturn(comment);
   when(event.getComment().getText()).thenReturn(commentText);
   pullRequestEvent = event;
  }
  final PullRequest pullRequest = mock(PullRequest.class);
  when(pullRequest.isClosed()).thenReturn(beingClosed);
  when(pullRequest.isOpen()).thenReturn(beingOpen);
  when(pullRequest.getId()).thenReturn(pullRequestId);
  when(pullRequest.getState()).thenReturn(pullRequestState);
  when(pullRequestEvent.getAction()).thenReturn(pullRequestAction);
  when(pullRequestEvent.getPullRequest()).thenReturn(pullRequest);
  when(pullRequestEvent.getPullRequest().getReviewers()).thenReturn(reviewers);
  when(pullRequestEvent.getPullRequest().getParticipants()).thenReturn(participants);
  when(pullRequestEvent.getPullRequest().getAuthor()).thenReturn(author);
  when(pullRequestEvent.getPullRequest().getFromRef()).thenReturn(fromRef);
  when(pullRequestEvent.getPullRequest().getToRef()).thenReturn(toRef);
  return pullRequestEvent;
 }

 public PrnfsTestBuilder triggerEvent() {
  return prnfsTestBuilder.trigger(build());
 }

 public PullRequestEventBuilder beingClosed() {
  beingClosed = true;
  return this;
 }

 public PullRequestEventBuilder withPullRequestId(Long id) {
  this.pullRequestId = id;
  return this;
 }

 public PullRequestEventBuilder withPullRequestInState(PullRequestState pullRequestState) {
  this.pullRequestState = pullRequestState;
  return this;
 }
}