using Business.Interfaces.Security;
using Entity.Dto.Security;
using Entity.Dto;
using Microsoft.AspNetCore.Mvc;
using WebA.Controllers.Interfaces.Security;
using WebA.Controllers.Interfaces.Operational;
using Business.Interfaces.Operational;
using Entity.Dto.Operational;

namespace WebA.Controllers.Implements.Operational
{
        [Route("api/LotFumigation")]
        [ApiController]
        public class LotFumigationController : ControllerBase, ILotFumigationController
        {
            protected readonly ILotFumigationBusiness business;

            public LotFumigationController(ILotFumigationBusiness business)
            {
                this.business = business;
            }

            [HttpDelete("{id}")]
            public async Task<ActionResult> Delete(int id)
            {
                await business.Delete(id);
                return NoContent();
            }

            [HttpGet("{id}")]
            public async Task<ActionResult<ApiResponse<LotFumigationDto>>> Get(int id)
            {
                var result = await business.GetById(id);
                if (result == null)
                {
                    return NotFound();
                }
                return Ok(result);
            }

            [HttpGet]
            public async Task<ActionResult<ApiResponse<IEnumerable<LotFumigationDto>>>> GetAll()
            {
                var result = await business.GetAll();
                return Ok(result);
            }

            [HttpGet("AllSelect")]
            public async Task<ActionResult<ApiResponse<IEnumerable<DataSelectDto>>>> GetAllSelect()
            {
                var result = await business.GetAllSelect();
                return Ok(result);
            }

            [HttpPost]
            public async Task<ActionResult> Post([FromBody] LotFumigationDto LotFumigation)
            {
                if (LotFumigation == null)
                {
                    return BadRequest("Entity is null");
                }
                var result = await business.Save(LotFumigation);
                return CreatedAtAction(nameof(Get), new { id = result.Id }, result);
            }

            [HttpPut]
            public async Task<ActionResult> Put([FromBody] LotFumigationDto LotFumigation)
            {
                if (LotFumigation == null)
                {
                    return BadRequest();
                }
                await business.Update(LotFumigation);
                return NoContent();
            }
        }
}
