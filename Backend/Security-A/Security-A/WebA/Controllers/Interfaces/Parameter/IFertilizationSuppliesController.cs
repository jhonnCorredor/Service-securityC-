using Entity.Dto;
using Microsoft.AspNetCore.Mvc;
using Entity.Dto.Parameter;

namespace WebA.Controllers.Interfaces.Parameter
{
    public interface IFertilizationSuppliesController
    {
        Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect();
        Task<ActionResult<ApiResponse<IEnumerable<FertilizationSuppliesDto>>>> GetAll();
        Task<ActionResult<ApiResponse<FertilizationSuppliesDto>>> Get(int id);
        Task<ActionResult> Post([FromBody] FertilizationSuppliesDto FertilizationSupplies);
        Task<ActionResult> Put([FromBody] FertilizationSuppliesDto FertilizationSupplies);
        Task<ActionResult> Delete(int id);
    }
}
