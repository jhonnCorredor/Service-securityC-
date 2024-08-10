using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto.Operational;
using Entity.Dto;
using Entity.Model.Operational;

namespace Business.Implements.Operational
{
    public class ReviewEvidenceBusiness : IReviewEvidenceBusiness
    {
        private readonly IReviewEvidenceData data;

        public ReviewEvidenceBusiness(IReviewEvidenceData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<ReviewEvidenceDto>> GetAll()
        {
            IEnumerable<ReviewEvidence> ReviewEvidences = await data.GetAll();
            var ReviewEvidenceDtos = ReviewEvidences.Select(ReviewEvidence => new ReviewEvidenceDto
            {
                Id = ReviewEvidence.Id,
                EvidenceId = (int)ReviewEvidence.EvidenceId,
                ReviewId = (int)ReviewEvidence.ReviewId,
                State = ReviewEvidence.State
            });

            return ReviewEvidenceDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<ReviewEvidenceDto> GetById(int id)
        {
            ReviewEvidence ReviewEvidence = await data.GetById(id);
            ReviewEvidenceDto dto = new ReviewEvidenceDto();
            dto.Id = ReviewEvidence.Id;
            dto.EvidenceId = (int) ReviewEvidence.EvidenceId;
            dto.ReviewId = (int)ReviewEvidence.ReviewId;
            dto.State = ReviewEvidence.State;
            return dto;
        }

        public ReviewEvidence mapearDatos(ReviewEvidence reviewEvidence, ReviewEvidenceDto entity)
        {
            reviewEvidence.Id = entity.Id;
            reviewEvidence.EvidenceId = entity.EvidenceId;
            reviewEvidence.ReviewId = entity.ReviewId;
            reviewEvidence.State = entity.State;
            return reviewEvidence;
        }

        public async Task<ReviewEvidence> Save(ReviewEvidenceDto entity)
        {
            ReviewEvidence ReviewEvidence = new ReviewEvidence();
            ReviewEvidence = mapearDatos(ReviewEvidence, entity);
            ReviewEvidence.Created_at = DateTime.Now;
            ReviewEvidence.Updated_at = null;
            ReviewEvidence.Deleted_at = null;

            return await data.Save(ReviewEvidence);
        }

        public async Task Update(ReviewEvidenceDto entity)
        {
            ReviewEvidence ReviewEvidence = await data.GetById(entity.Id);
            if (ReviewEvidence == null)
            {
                throw new Exception("Registro no encontrado");
            }
            ReviewEvidence = mapearDatos(ReviewEvidence, entity);
            ReviewEvidence.Updated_at = DateTime.Now;

            await data.Update(ReviewEvidence);
        }
    }
}
