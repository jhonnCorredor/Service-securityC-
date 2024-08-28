using Business.Interfaces.Security;
using Data.Interfaces.Security;
using Entity.Dto.Security;
using Entity.Dto;
using Entity.Model.Security;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Business.Interfaces.Operational;
using Data.Interfaces.Operational;
using Entity.Dto.Operational;

namespace Business.Implements.Operational
{
    public class LotFumigationBusiness : ILotFumigationBusiness
    {
        protected readonly ILotFumigationData data;

        public LotFumigationBusiness(ILotFumigationData data)
        {
            this.data = data;
        }

        public async Task Delete(int id)
        {
            await data.Delete(id);
        }

        public async Task<IEnumerable<LotFumigationDto>> GetAll()
        {
            IEnumerable<LotFumigation> LotFumigations = await data.GetAll();
            var LotFumigationDtos = LotFumigations.Select(LotFumigation => new LotFumigationDto
            {
                Id = LotFumigation.Id,
                LotId = LotFumigation.LotId,
                FumigationId = LotFumigation.FumigationId,
                State = LotFumigation.State
            });

            return LotFumigationDtos;
        }

        public async Task<IEnumerable<DataSelectDto>> GetAllSelect()
        {
            return await data.GetAllSelect();
        }

        public async Task<LotFumigationDto> GetById(int id)
        {
            LotFumigation LotFumigation = await data.GetById(id);
            LotFumigationDto LotFumigationDto = new LotFumigationDto();

            LotFumigationDto.Id = LotFumigation.Id;
            LotFumigationDto.LotId = LotFumigation.LotId;
            LotFumigationDto.FumigationId = LotFumigation.FumigationId;
            LotFumigationDto.State = LotFumigation.State;
            return LotFumigationDto;
        }

        public LotFumigation mapearDatos(LotFumigation LotFumigation, LotFumigationDto entity)
        {
            LotFumigation.Id = entity.Id;
            LotFumigation.LotId = entity.LotId;
            LotFumigation.FumigationId = entity.FumigationId;
            LotFumigation.State = entity.State;
            return LotFumigation;
        }

        public async Task<LotFumigation> Save(LotFumigationDto entity)
        {
            LotFumigation LotFumigation = new LotFumigation();
            LotFumigation = mapearDatos(LotFumigation, entity);
            LotFumigation.Created_at = DateTime.Now;
            LotFumigation.Deleted_at = null;
            LotFumigation.Updated_at = null;

            return await data.Save(LotFumigation);
        }

        public async Task Update(LotFumigationDto entity)
        {
            LotFumigation LotFumigation = await data.GetById(entity.Id);
            if (LotFumigation == null)
            {
                throw new Exception("Registro no encontrado");
            }
            LotFumigation = mapearDatos(LotFumigation, entity);
            LotFumigation.Updated_at = DateTime.Now;

            await data.Update(LotFumigation);
        }
    }
}
