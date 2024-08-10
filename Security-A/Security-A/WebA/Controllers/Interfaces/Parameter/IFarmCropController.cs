using Entity.Dto;
using Microsoft.AspNetCore.Mvc;
using Entity.Dto.Parameter;

namespace WebA.Controllers.Interfaces.Parameter
{
    public interface IFarmCropController
    {
        Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect();
        Task<ActionResult<ApiResponse<IEnumerable<FarmCropDto>>>> GetAll();
        Task<ActionResult<ApiResponse<FarmCropDto>>> Get(int id);
        Task<ActionResult> Post([FromBody] FarmCropDto FarmCrop);
        Task<ActionResult> Put([FromBody] FarmCropDto FarmCrop);
        Task<ActionResult> Delete(int id);
    }
}
