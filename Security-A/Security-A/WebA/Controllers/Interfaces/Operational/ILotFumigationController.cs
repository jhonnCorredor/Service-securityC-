using Entity.Dto.Security;
using Entity.Dto;
using Microsoft.AspNetCore.Mvc;
using Entity.Dto.Operational;

namespace WebA.Controllers.Interfaces.Operational
{
    public interface ILotFumigationController
    {
        Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect();
        Task<ActionResult<ApiResponse<IEnumerable<LotFumigationDto>>>> GetAll();
        Task<ActionResult<ApiResponse<LotFumigationDto>>> Get(int id);
        Task<ActionResult> Post([FromBody] LotFumigationDto LotFumigation);
        Task<ActionResult> Put([FromBody] LotFumigationDto LotFumigation);
        Task<ActionResult> Delete(int id);
    }
}
