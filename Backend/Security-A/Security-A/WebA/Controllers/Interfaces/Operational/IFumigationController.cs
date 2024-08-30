using Entity.Dto.Operational;
using Entity.Dto;
using Microsoft.AspNetCore.Mvc;

namespace WebA.Controllers.Interfaces.Operational
{
    public interface IFumigationController
    {
        Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect();
        Task<ActionResult<ApiResponse<IEnumerable<FumigationDto>>>> GetAll();
        Task<ActionResult<ApiResponse<FumigationDto>>> Get(int id);
        Task<ActionResult> Post([FromBody] FumigationDto Fumigation);
        Task<ActionResult> Put([FromBody] FumigationDto Fumigation);
        Task<ActionResult> Delete(int id);
    }
}
