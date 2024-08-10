using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto.Operational;
using Entity.Dto;
using Entity.Model.Operational;

namespace Business.Implements.Operational
{
    public class ReviewTechnicalBusiness : IReviewTechnicalBusiness
    {
        private readonly IReviewTechnicalData data;

        public ReviewTechnicalBusiness(IReviewTechnicalData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<ReviewTechnicalDto>> GetAll()
        {
            IEnumerable<ReviewTechnical> ReviewTechnicals = await data.GetAll();
            var ReviewTechnicalDtos = ReviewTechnicals.Select(ReviewTechnical => new ReviewTechnicalDto
            {
                Id = ReviewTechnical.Id,
                ChecklistId = ReviewTechnical.ChecklistId,
                Code = ReviewTechnical.Code,
                Date_review = ReviewTechnical.Date_review,
                FarmId = ReviewTechnical.FarmId,
                Observation = ReviewTechnical.Observation,
                TecnicoId = ReviewTechnical.TecnicoId,
                State = ReviewTechnical.State
            });

            return ReviewTechnicalDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<ReviewTechnicalDto> GetById(int id)
        {
            ReviewTechnical ReviewTechnical = await data.GetById(id);
            ReviewTechnicalDto dto = new ReviewTechnicalDto();
            dto.Id = ReviewTechnical.Id;
            dto.ChecklistId = ReviewTechnical.ChecklistId;
            dto.Code = ReviewTechnical.Code;
            dto.Date_review = ReviewTechnical.Date_review;
            dto.FarmId = ReviewTechnical.FarmId;
            dto.Observation = ReviewTechnical.Observation;
            dto.TecnicoId= ReviewTechnical.TecnicoId;
            dto.State = ReviewTechnical.State;
            return dto;
        }

        public ReviewTechnical mapearDatos(ReviewTechnical reviewTechnical, ReviewTechnicalDto entity)
        {
            reviewTechnical.Id = entity.Id;
            reviewTechnical.ChecklistId = entity.ChecklistId;
            reviewTechnical.Code = entity.Code;
            reviewTechnical.Date_review = entity.Date_review;
            reviewTechnical.FarmId = entity.FarmId;
            reviewTechnical.Observation = entity.Observation;
            reviewTechnical.TecnicoId = entity.TecnicoId;
            reviewTechnical.State = entity.State;
            return reviewTechnical;
        }

        public async Task<ReviewTechnical> Save(ReviewTechnicalDto entity)
        {
            ReviewTechnical ReviewTechnical = new ReviewTechnical();
            ReviewTechnical = mapearDatos(ReviewTechnical, entity);
            ReviewTechnical.Created_at = DateTime.Now;
            ReviewTechnical.Updated_at = null;
            ReviewTechnical.Deleted_at = null;

            return await data.Save(ReviewTechnical);
        }

        public async Task Update(ReviewTechnicalDto entity)
        {
            ReviewTechnical ReviewTechnical = await data.GetById(entity.Id);
            if (ReviewTechnical == null)
            {
                throw new Exception("Registro no encontrado");
            }
            ReviewTechnical = mapearDatos(ReviewTechnical, entity);
            ReviewTechnical.Updated_at = DateTime.Now;

            await data.Update(ReviewTechnical);
        }
    }
}
