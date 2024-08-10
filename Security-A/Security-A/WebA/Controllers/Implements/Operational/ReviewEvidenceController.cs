using Business.Interfaces.Operational;
using Entity.Dto.Operational;
using Entity.Dto;
using Microsoft.AspNetCore.Mvc;
using WebA.Controllers.Interfaces.Operational;

namespace WebA.Controllers.Implements.Operational
{
    [Route("api/ReviewEvidence")]
    [ApiController]
    public class ReviewEvidenceController : ControllerBase, IReviewEvidenceController
    {
        private readonly IReviewEvidenceBusiness business;

        public ReviewEvidenceController(IReviewEvidenceBusiness business)
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
        public async Task<ActionResult<ApiResponse<ReviewEvidenceDto>>> Get(int id)
        {
            var result = await business.GetById(id);
            if (result == null)
            {
                return NotFound();
            }
            return Ok(result);
        }

        [HttpGet]
        public async Task<ActionResult<ApiResponse<IEnumerable<ReviewEvidenceDto>>>> GetAll()
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
        public async Task<ActionResult> Post([FromBody] ReviewEvidenceDto ReviewEvidence)
        {
            if (ReviewEvidence == null)
            {
                return BadRequest("Entity is null.");
            }
            var result = await business.Save(ReviewEvidence);
            return CreatedAtAction(nameof(Get), new { id = result.Id }, result);
        }

        [HttpPut]
        public async Task<ActionResult> Put([FromBody] ReviewEvidenceDto ReviewEvidence)
        {
            if (ReviewEvidence == null)
            {
                return BadRequest();
            }
            await business.Update(ReviewEvidence);
            return NoContent();
        }
    }
}
