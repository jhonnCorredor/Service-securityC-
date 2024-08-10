using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto.Operational;
using Entity.Dto;
using Entity.Model.Operational;

namespace Business.Implements.Operational
{
    public class QualificationBusiness : IQualificationBusiness
    {
        private readonly IQualificationData data;

        public QualificationBusiness(IQualificationData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<QualificationDto>> GetAll()
        {
            IEnumerable<Qualification> Qualifications = await data.GetAll();
            var QualificationDtos = Qualifications.Select(Qualification => new QualificationDto
            {
                Id = Qualification.Id,
                AssesmentCriteriaId = Qualification.AssesmentCriteriaId,
                ChecklistId = Qualification.ChecklistId,
                Observation = Qualification.Observation,
                Qualification_criteria = Qualification.Qualification_criteria,
                State = Qualification.State
            });

            return QualificationDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<QualificationDto> GetById(int id)
        {
            Qualification Qualification = await data.GetById(id);
            QualificationDto dto = new QualificationDto();
            dto.Id = Qualification.Id;
            dto.AssesmentCriteriaId = Qualification.AssesmentCriteriaId;
            dto.ChecklistId = Qualification.ChecklistId;
            dto.Observation = Qualification.Observation;
            dto.Qualification_criteria = Qualification.Qualification_criteria;
            dto.State = Qualification.State;
            return dto;
        }

        public Qualification mapearDatos(Qualification qualification, QualificationDto entity)
        {
            qualification.Id = entity.Id;
            qualification.AssesmentCriteriaId = entity.AssesmentCriteriaId;
            qualification.ChecklistId = entity.ChecklistId;
            qualification.Observation = entity.Observation;
            qualification.Qualification_criteria = entity.Qualification_criteria;
            qualification.State = entity.State;
            return qualification;
        }

        public async Task<Qualification> Save(QualificationDto entity)
        {
            Qualification Qualification = new Qualification();
            Qualification = mapearDatos(Qualification, entity);
            Qualification.Created_at = DateTime.Now;
            Qualification.Updated_at = null;
            Qualification.Deleted_at = null;

            return await data.Save(Qualification);
        }

        public async Task Update(QualificationDto entity)
        {
            Qualification Qualification = await data.GetById(entity.Id);
            if (Qualification == null)
            {
                throw new Exception("Registro no encontrado");
            }
            Qualification = mapearDatos(Qualification, entity);
            Qualification.Updated_at = DateTime.Now;

            await data.Update(Qualification);
        }
    }
}
