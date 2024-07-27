package jo.jpashop.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jo.jpashop.domain.Member;
import jo.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Member", description = "회원 관련 API")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 등록 V1: 요청 값으로 Member 엔티티를 직접 받는다.
     * 문제점
     * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
     *  - 엔티티에 API 검증을 위한 로직이 들어간다.(@NotEmpty 등등)
     *  - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요청 요구사항을 담기는 어렵다.
     *  - 엔티티가 변경되면 API 스펙이 변한다.
     * 결론
     * - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
     * @param member
     * @return
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 등록 V2: 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다.
     * @param request
     * @return
     */
    @PostMapping("/api/v2/members")
    @Operation(summary = "회원 등록 API")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 수정 API
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/api/v2/members/{id}")
    @Operation(summary = "회원 정보 수정 API")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    @Schema(title = "회원 정보 수정 요청 DTO")
    static class UpdateMemberRequest {
        @Schema(description = "회원 이름")
        private String name;
    }

    @Data
    @Schema(title = "회원 정보 수정 응답 DTO")
    @AllArgsConstructor
    static class UpdateMemberResponse {
        @Schema(description = "회원 id")
        private Long id;

        @Schema(description = "회원 이름")
        private String name;
    }

    @Data
    @Schema(title = "회원 등록 요청 DTO")
    static class CreateMemberRequest {
        @NotEmpty
        @Schema(description = "회원 이름")
        private String name;
    }

    @Data
    @Schema(title = "회원 등록 응답 DTO")
    static class CreateMemberResponse {
        @Schema(description = "회원 id")
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
