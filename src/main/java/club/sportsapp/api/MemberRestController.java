package club.sportsapp.api;

import club.sportsapp.core.exceptions.*;
import club.sportsapp.core.filters.MemberFilters;
import club.sportsapp.dto.*;
import club.sportsapp.service.IMemberService;
import club.sportsapp.validator.MemberInsertValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberRestController {

    private final IMemberService memberService;
    private final MemberInsertValidator memberInsertValidator;

    @Operation(
            summary = "Save a member",
            description = "Registers a new member in the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Member created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberReadOnlyDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409", description = "Member already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponseDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<MemberReadOnlyDTO> saveMember(@Valid @RequestBody MemberInsertDTO dto,
                                                        BindingResult bindingResult)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, ValidationException {

        memberInsertValidator.validate(dto, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new ValidationException("Member", "Invalid member data", bindingResult);
        }

        MemberReadOnlyDTO memberReadOnlyDTO = memberService.saveMember(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{uuid}")
                .buildAndExpand(memberReadOnlyDTO.uuid())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(memberReadOnlyDTO);
    }

    @Operation(
            summary = "Upload Membership ID attachment file for a member",
            description = "Uploads a member's membership document file. Replaces existing file if present."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "File uploaded successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Member not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "File upload failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping("/{uuid}/membership-file")
    public ResponseEntity<Void> uploadMembershipFile(@PathVariable UUID uuid, @RequestParam("file") MultipartFile file)
            throws FileUploadException, EntityNotFoundException {

        memberService.saveMembershipIdFile(uuid, file);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a member")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Member updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberReadOnlyDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409", description = "Member already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(responseCode = "404", description = "Member not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500", description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401", description = "Not Authenticated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403", description = "Access Denied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PutMapping("/{uuid}")
    public ResponseEntity<MemberReadOnlyDTO> updateMember(@Valid @RequestBody MemberUpdateDTO dto, BindingResult bindingResult)
        throws ValidationException, EntityAlreadyExistsException, EntityNotFoundException, EntityInvalidArgumentException {



        if (bindingResult.hasErrors()) {
            throw new ValidationException("Member", "Invalid member data", bindingResult);
        }

        MemberReadOnlyDTO memberReadOnlyDTO = memberService.updateMember(dto);

        return ResponseEntity.ok(memberReadOnlyDTO);
    }

    @Operation(summary = "Deletes a member. It is a soft-delete design pattern.")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Member deleted",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Member not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403", description = "Access Denied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{uuid}")
    public ResponseEntity<MemberReadOnlyDTO> deleteMemberByUuid(@PathVariable UUID uuid) throws EntityNotFoundException {

        MemberReadOnlyDTO memberReadOnlyDTO = memberService.deleteMemberByUUID(uuid);

        return ResponseEntity.ok(memberReadOnlyDTO);
    }

    //@GetMapping("/{uuid}")
    public ResponseEntity<MemberReadOnlyDTO> getMemberByUuid(@PathVariable UUID uuid) throws EntityNotFoundException {

        MemberReadOnlyDTO memberReadOnlyDTO = memberService.getMemberByUUID(uuid);

        return ResponseEntity.ok(memberReadOnlyDTO);
    }

    @Operation(summary = "Get one non-deleted member by uuid")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Member returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberReadOnlyDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Member not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401", description = "Not Authenticated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403", description = "Access Denied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<MemberReadOnlyDTO> getMemberByUuidAndDeletedFalse(@PathVariable UUID uuid) throws EntityNotFoundException {

        MemberReadOnlyDTO memberReadOnlyDTO = memberService.getMemberByUUIDAndDeletedFalse(uuid);

        return ResponseEntity.ok(memberReadOnlyDTO);
    }

    @Operation(summary = "Get all members paginated and filtered")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Members returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403", description = "Access Denied",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<Page<MemberReadOnlyDTO>> getMembersFiltered(
            @PageableDefault(page = 0, size = 5) Pageable pageable,
            @ModelAttribute MemberFilters filters)
            throws EntityNotFoundException {
        Page<MemberReadOnlyDTO> dtoPage = memberService.getPaginatedMembersFiltered(pageable, filters);
        return ResponseEntity.ok(dtoPage);
    }

}
