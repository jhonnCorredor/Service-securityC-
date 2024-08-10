using Entity.Dto.Operational;
using Entity.Dto;
using Microsoft.AspNetCore.Mvc;

namespace WebA.Controllers.Interfaces.Operational
{
    public interface IReviewEvidenceController
    {
        Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect();
        Task<ActionResult<ApiResponse<IEnumerable<ReviewEvidenceDto>>>> GetAll();
        Task<ActionResult<ApiResponse<ReviewEvidenceDto>>> Get(int id);
        Task<ActionResult> Post([FromBody] ReviewEvidenceDto ReviewEvidence);
        Task<ActionResult> Put([FromBody] ReviewEvidenceDto ReviewEvidence);
        Task<ActionResult> Delete(int id);
    }
}
