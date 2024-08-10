using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Operational;

namespace Business.Interfaces.Operational
{
    public interface IReviewEvidenceBusiness
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<ReviewEvidenceDto> GetById(int id);
        Task<ReviewEvidence> Save(ReviewEvidenceDto entity);
        Task Update(ReviewEvidenceDto entity);
        ReviewEvidence mapearDatos(ReviewEvidence reviewEvidence, ReviewEvidenceDto entity);
        Task<IEnumerable<ReviewEvidenceDto>> GetAll();
    }
}
