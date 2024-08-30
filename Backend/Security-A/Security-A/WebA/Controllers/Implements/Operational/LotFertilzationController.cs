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
        [Route("api/LotFertilization")]
        [ApiController]
        public class LotFertilizationController : ControllerBase, ILotFertilizationController
        {
            protected readonly ILotFertilizationBusiness business;

            public LotFertilizationController(ILotFertilizationBusiness business)
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
            public async Task<ActionResult<ApiResponse<LotFertilizationDto>>> Get(int id)
            {
                var result = await business.GetById(id);
                if (result == null)
                {
                    return NotFound();
                }
                return Ok(result);
            }

            [HttpGet]
            public async Task<ActionResult<ApiResponse<IEnumerable<LotFertilizationDto>>>> GetAll()
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
            public async Task<ActionResult> Post([FromBody] LotFertilizationDto LotFertilization)
            {
                if (LotFertilization == null)
                {
                    return BadRequest("Entity is null");
                }
                var result = await business.Save(LotFertilization);
                return CreatedAtAction(nameof(Get), new { id = result.Id }, result);
            }

            [HttpPut]
            public async Task<ActionResult> Put([FromBody] LotFertilizationDto LotFertilization)
            {
                if (LotFertilization == null)
                {
                    return BadRequest();
                }
                await business.Update(LotFertilization);
                return NoContent();
            }
        }
}
