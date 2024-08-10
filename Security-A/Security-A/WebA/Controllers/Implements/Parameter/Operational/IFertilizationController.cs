using Entity.Dto.Operational;
using Entity.Dto;
using Microsoft.AspNetCore.Mvc;

namespace WebA.Controllers.Implements.Parameter.Operational
{
    public interface IFertilizationController
    {
        Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect();
        Task<ActionResult<ApiResponse<IEnumerable<FertilizationDto>>>> GetAll();
        Task<ActionResult<ApiResponse<FertilizationDto>>> Get(int id);
        Task<ActionResult> Post([FromBody] FertilizationDto Fertilization);
        Task<ActionResult> Put([FromBody] FertilizationDto Fertilization);
        Task<ActionResult> Delete(int id);
    }
}
