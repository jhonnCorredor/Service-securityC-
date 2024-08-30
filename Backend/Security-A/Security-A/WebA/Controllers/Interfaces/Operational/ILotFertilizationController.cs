using Entity.Dto.Security;
using Entity.Dto;
using Microsoft.AspNetCore.Mvc;
using Entity.Dto.Operational;

namespace WebA.Controllers.Interfaces.Operational
{
    public interface ILotFertilizationController
    {
        Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect();
        Task<ActionResult<ApiResponse<IEnumerable<LotFertilizationDto>>>> GetAll();
        Task<ActionResult<ApiResponse<LotFertilizationDto>>> Get(int id);
        Task<ActionResult> Post([FromBody] LotFertilizationDto LotFertilization);
        Task<ActionResult> Put([FromBody] LotFertilizationDto LotFertilization);
        Task<ActionResult> Delete(int id);
    }
}
