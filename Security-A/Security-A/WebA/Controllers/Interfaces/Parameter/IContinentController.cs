using Entity.Dto;
using Entity.Dto.Parameter;
using Microsoft.AspNetCore.Mvc;

namespace WebA.Controllers.Interfaces.Parameter
{
    public interface IContinentController
    {
        Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect();
        Task<ActionResult<ApiResponse<IEnumerable<ContinentDto>>>> GetAll();
        Task<ActionResult<ApiResponse<ContinentDto>>> Get(int id);
        Task<ActionResult> Post([FromBody] ContinentDto continent);
        Task<ActionResult> Put([FromBody] ContinentDto continent);
        Task<ActionResult> Delete(int id);
    }
}
