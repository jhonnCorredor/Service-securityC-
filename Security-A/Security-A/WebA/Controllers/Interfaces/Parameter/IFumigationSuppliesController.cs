using Entity.Dto;
using Microsoft.AspNetCore.Mvc;
using Entity.Dto.Parameter;

namespace WebA.Controllers.Interfaces.Parameter
{
    public interface IFumigationSuppliesController
    {
        Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect();
        Task<ActionResult<ApiResponse<IEnumerable<FumigationSuppliesDto>>>> GetAll();
        Task<ActionResult<ApiResponse<FumigationSuppliesDto>>> Get(int id);
        Task<ActionResult> Post([FromBody] FumigationSuppliesDto FumigationSupplies);
        Task<ActionResult> Put([FromBody] FumigationSuppliesDto FumigationSupplies);
        Task<ActionResult> Delete(int id);
    }
}
