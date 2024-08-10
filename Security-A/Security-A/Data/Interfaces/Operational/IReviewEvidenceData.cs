using Entity.Dto;
using Entity.Model.Operational;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Data.Interfaces.Operational
{
    public interface IReviewEvidenceData
    {
        Task Delete(int id);
        Task<IEnumerable<DataSelectDto>> GetAllSelect();
        Task<ReviewEvidence> GetById(int id);
        Task<ReviewEvidence> Save(ReviewEvidence entity);
        Task Update(ReviewEvidence entity);
        Task<IEnumerable<ReviewEvidence>> GetAll();
    }
}
