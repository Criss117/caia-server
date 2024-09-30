package com.solidos.caia.api.papers;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.solidos.caia.api.common.models.CommonResponse;
import com.solidos.caia.api.papers.dto.ChangeStateDto;
import com.solidos.caia.api.papers.dto.CreatePaperDto;
import com.solidos.caia.api.papers.dto.ListPapersDto;
import com.solidos.caia.api.papers.entities.PaperEntity;
import com.solidos.caia.api.papers.entities.PaperReviewerEntity;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/papers")
@PreAuthorize("authenticated")
public class PaperController {
  private PaperService paperService;

  public PaperController(PaperService paperService) {
    this.paperService = paperService;
  }

  @PostMapping
  public ResponseEntity<CommonResponse<PaperEntity>> createPaper(
      @ModelAttribute @Valid CreatePaperDto createPaperDto, MultipartFile file) throws Exception {
    PaperEntity newPaper = paperService.createpaper(createPaperDto, file);

    return ResponseEntity.ok(CommonResponse.success(newPaper, "Paper created"));
  }

  @GetMapping("/by-conference/{conferenceId}")
  public ResponseEntity<CommonResponse<ListPapersDto>> findAllPapers(@PathVariable @Valid Long conferenceId) {
    ListPapersDto papers = paperService.findAllPapers(conferenceId);

    return ResponseEntity.ok(CommonResponse.success(papers, "Papers found"));
  }

  @GetMapping("/{paperId}")
  public ResponseEntity<CommonResponse<PaperEntity>> getPaper(@PathVariable Long paperId) {
    PaperEntity paper = paperService.findById(paperId);

    return ResponseEntity.ok(CommonResponse.success(paper, "Paper found"));
  }

  @PostMapping("/{paperId}/new-reviewer/{userId}")
  public ResponseEntity<CommonResponse<PaperReviewerEntity>> addNewReviewer(@PathVariable Long paperId,
      @PathVariable Long userId) {
    PaperReviewerEntity paperReviewer = paperService.addNewReviewer(paperId, userId);

    return ResponseEntity.ok(CommonResponse.success(paperReviewer, "Reviewer added"));
  }

  @GetMapping("/by-conference/{conferenceId}/to-review")
  public ResponseEntity<CommonResponse<List<PaperEntity>>> getAllReviewers(@PathVariable @Valid Long conferenceId) {
    List<PaperEntity> papers = paperService.findAllByUserId(conferenceId);

    return ResponseEntity.ok(CommonResponse.success(papers, "Reviewers found"));
  }

  @PutMapping("/{paperId}/state")
  public ResponseEntity<CommonResponse<PaperEntity>> changePaperState(@PathVariable @Validated Long paperId,
      @RequestBody ChangeStateDto changeStateDto) {
    PaperEntity paper = paperService.updatePaperState(paperId, changeStateDto.getState());

    return ResponseEntity.ok(CommonResponse.success(paper, "State updated"));
  }
}
