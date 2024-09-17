using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto;
using Entity.Dto.Operational;
using Entity.Model.Operational;

namespace Business.Implements.Operational
{
    public class TreatmentBusiness : ITreatmentBusiness
    {
        private readonly ITreatmentData data;

        public TreatmentBusiness(ITreatmentData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<TreatmentDto>> GetAll()
        {
            IEnumerable<Treatment> Treatments = await data.GetAll();
            var TreatmentDtos = Treatments.Select(Treatment => new TreatmentDto
            {
                Id = Treatment.Id,
                DateTreatment = Treatment.DateTreatment,
                QuantityMix = Treatment.QuantityMix,
                TypeTreatment = Treatment.TypeTreatment,
                State = Treatment.State
            });

            return TreatmentDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<TreatmentDto> GetById(int id)
        {
            Treatment Treatment = await data.GetById(id);
            TreatmentDto dto = new TreatmentDto();
            dto.Id = Treatment.Id;
            dto.DateTreatment = Treatment.DateTreatment;
            dto.QuantityMix = Treatment.QuantityMix;
            dto.TypeTreatment = Treatment.TypeTreatment;
            dto.State = Treatment.State;
            return dto;
        }

        public Treatment mapearDatos(Treatment treatment, TreatmentDto entity)
        {
            treatment.Id = entity.Id;
            treatment.DateTreatment = entity.DateTreatment;
            treatment.QuantityMix = entity.QuantityMix;
            treatment.TypeTreatment = entity.TypeTreatment;
            treatment.State = entity.State;
            return treatment;
        }

        public async Task<Treatment> Save(TreatmentDto entity)
        {
            Treatment Treatment = new Treatment();
            Treatment = mapearDatos(Treatment, entity);
            Treatment.CreatedAt = DateTime.Now;
            Treatment.State = true;
            Treatment.UpdatedAt = null;
            Treatment.DeletedAt = null;

            return await data.Save(Treatment);
        }

        public async Task Update(TreatmentDto entity)
        {
            Treatment Treatment = await data.GetById(entity.Id);
            if (Treatment == null)
            {
                throw new Exception("Registro no encontrado");
            }
            Treatment = mapearDatos(Treatment, entity);
            Treatment.UpdatedAt = DateTime.Now;

            await data.Update(Treatment);
        }
    }
}
