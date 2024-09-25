package com.solidos.caia.api.papers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.solidos.caia.api.common.enums.RoleEnum;
import com.solidos.caia.api.common.utils.PaperKeys;
import com.solidos.caia.api.conferences.entities.ConferenceEntity;
import com.solidos.caia.api.members.MemberService;
import com.solidos.caia.api.members.MembersPermissions;
import com.solidos.caia.api.members.dto.CreateMemberDto;
import com.solidos.caia.api.papers.dto.CreatePaperDto;
import com.solidos.caia.api.papers.dto.ListPapersDto;
import com.solidos.caia.api.papers.entities.PaperEntity;
import com.solidos.caia.api.papers.repositories.PaperRepository;
import com.solidos.caia.api.users.entities.UserEntity;

import jakarta.transaction.Transactional;

@Service
public class PaperService {

  private final PaperRepository paperRepository;
  private final MemberService memberService;
  private final MembersPermissions membersPermissions;

  public PaperService(
      PaperRepository paperRepository,
      MembersPermissions membersPermissions,
      MemberService memberService) {
    this.paperRepository = paperRepository;
    this.membersPermissions = membersPermissions;
    this.memberService = memberService;
  }

  @Transactional
  public PaperEntity createpaper(CreatePaperDto createPaperDto, MultipartFile file) throws Exception {
    Long userId = membersPermissions.hasConferencePermission(createPaperDto.getConferenceId(), RoleEnum.ORGANIZER);

    String fileName = this.uploadFile(file);

    PaperEntity paperEntity = PaperEntity.builder()
        .title(createPaperDto.getTitle())
        .description(createPaperDto.getDescription())
        .fileName(fileName)
        .keys(PaperKeys.toString(createPaperDto.getKeys()))
        .userEntity(UserEntity.builder().id(userId).build())
        .conferenceEntity(ConferenceEntity.builder().id(createPaperDto.getConferenceId()).build())
        .build();

    if (paperEntity == null) {
      throw new Exception("PaperEntity cannot be null after being built");
    }

    try {
      PaperEntity savedPaper = paperRepository.save(paperEntity);

      var existingMember = memberService.findByComposeId(createPaperDto.getConferenceId(), userId, RoleEnum.AUTHOR);

      if (existingMember.isPresent()) {
        return savedPaper;
      }

      CreateMemberDto newMember = CreateMemberDto.builder()
          .role(RoleEnum.AUTHOR)
          .userId(userId)
          .conferenceId(createPaperDto.getConferenceId())
          .build();

      memberService.createMember(newMember);
      return savedPaper;
    } catch (Exception e) {
      throw new Exception("Error creating paper");
    }
  }

  public String uploadFile(MultipartFile file) throws Exception {
    try {
      String fileName = UUID.randomUUID().toString();
      byte[] bytes = file.getBytes();
      String originalName = file.getOriginalFilename();

      long fileSize = file.getSize();
      long maxFileSize = 5 * 1024 * 1024;

      if (fileSize > maxFileSize) {
        throw new Exception("File size is too large");
      }

      if (originalName == null || !originalName.endsWith(".pdf")) {
        throw new Exception("Only PDF files are allowed");
      }

      String fileExtension = originalName.substring(originalName.lastIndexOf("."));

      String newFileName = fileName + fileExtension;

      File folder = new File("src/main/resources/papers");

      if (!folder.exists()) {
        folder.mkdirs();
      }

      Path path = Paths.get("src/main/resources/papers/" + newFileName);

      Files.write(path, bytes);

      return newFileName;

    } catch (Exception e) {
      throw new Exception("Error uploading file");
    }
  }

  public ListPapersDto findAllPapers(Long conferenceId) {
    List<RoleEnum> userRole = membersPermissions.getUserRole(conferenceId);
    List<PaperEntity> papers = paperRepository.findAllByConferenceId(conferenceId);

    return ListPapersDto.builder()
        .papers(papers)
        .withRole(userRole)
        .build();
  }
}
